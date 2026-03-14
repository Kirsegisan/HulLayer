package com.kirsegisan.hullayer.data.repository

import android.app.Activity
import android.app.RecoverableSecurityException
import android.content.ContentUris
import android.content.Context
import android.database.ContentObserver
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.core.net.toUri
import com.kirsegisan.hullayer.data.datastore.OrderMediaQueue
import com.kirsegisan.hullayer.data.datastore.SettingDataStoreRepository
import com.kirsegisan.hullayer.data.values.TrackInfo

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.withContext

class TrackRepositoryImpl(
    private val context: Context,
    private val settingsRepository: SettingDataStoreRepository
) : TrackRepository {

    private val repoScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val sharedTracksFlow: SharedFlow<List<TrackInfo>> = createSharedTracksFlow()

    ///////////////////////////////////////////////
    // Fetching favourites and combine with all songs
    ///////////////////////////////////////////////
    private fun createSharedTracksFlow(): SharedFlow<List<TrackInfo>> {
        val mediaStoreTrigger = callbackFlow {
            val observer = object : ContentObserver(Handler(Looper.getMainLooper())) {
                override fun onChange(selfChange: Boolean) { trySend(Unit) }
            }
            context.contentResolver.registerContentObserver(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, true, observer
            )
            trySend(Unit)
            awaitClose { context.contentResolver.unregisterContentObserver(observer) }
        }

        return mediaStoreTrigger.map {
            val prefs = settingsRepository.settingsPreferencesFlow.first()
            fetchTracksFromMediaStore(
                prefs.trackPrimaryOrder,
                prefs.trackSecondaryOrder,
                prefs.trackSortAscending
            )
        }.flowOn(Dispatchers.IO).shareIn(
            scope = repoScope,
            started = SharingStarted.Lazily,
            replay = 1
        )
    }

    override fun getAllTracks(): Flow<List<TrackInfo>> {
        return sharedTracksFlow
    }

    ///////////////////////////////////////////////
    // Fetching all songs from MediaStore
    ///////////////////////////////////////////////
    private fun fetchTracksFromMediaStore(
        primaryOrder: OrderMediaQueue,
        secondaryOrder: OrderMediaQueue,
        isAsc: Boolean,
        limit: Int = Int.MAX_VALUE,
        offset: Int = 0
    ): List<TrackInfo> {
        val mediaList = ArrayList<TrackInfo>()
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.ALBUM_ID
        )

        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"
        val sortOrder = getSortOrder(primaryOrder, secondaryOrder, isAsc)
        Log.d("MediaStore", "SortOrder: $sortOrder")

        val finalSortOrder = if (limit != Int.MAX_VALUE)
            "$sortOrder LIMIT $limit OFFSET $offset" else sortOrder


        try {
            val cursor = context.contentResolver.query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                null,
                finalSortOrder
            )

            cursor?.use { c ->
                val idColumn = c.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
                val titleColumn = c.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
                val artistColumn = c.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
                val albumColumn = c.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
                val durationColumn = c.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
                val albumIdColumn = c.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)

                while (c.moveToNext()) {
                    val id = c.getLong(idColumn)
                    val title = c.getString(titleColumn) ?: ""
                    val artist = c.getString(artistColumn) ?: ""
                    val album = c.getString(albumColumn) ?: ""
                    val duration = c.getLong(durationColumn)
                    val albumId = c.getLong(albumIdColumn)

                    val contentUri = ContentUris.withAppendedId(
                        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                        id
                    ).toString()

                    val coverUri = ContentUris.withAppendedId(
                        "content://media/external/audio/albumart".toUri(),
                        albumId
                    ).toString()

                    mediaList.add(
                        TrackInfo(
                            id = id,
                            title = title,
                            artist = artist,
                            album = album,
                            duration = duration,
                            uri = contentUri,
                            cover = coverUri
                        )
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return mediaList
    }

    override suspend fun getTracksPaged(page: Int, pageSize: Int): List<TrackInfo> {
        val prefs = settingsRepository.settingsPreferencesFlow.first()
        val offset = page * pageSize

        return fetchTracksFromMediaStore(
                primaryOrder = prefs.trackPrimaryOrder,
                secondaryOrder = prefs.trackSecondaryOrder,
                isAsc = prefs.trackSortAscending,
                limit = pageSize,
                offset = offset
            )
    }

    override suspend fun getTrackById(trackId: Int): TrackInfo? {
        return withContext(Dispatchers.IO) {
            fetchTrackFromMediaStore(trackId)
        }
    }

    ///////////////////////////////////////////////
    // Fetching track by id from MediaStore
    ///////////////////////////////////////////////
    private fun fetchTrackFromMediaStore(trackId: Int): TrackInfo? {
        val uri: Uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val resolver = context.contentResolver
        val selection = "${MediaStore.Audio.Media._ID} = ?"
        val selectionArgs = arrayOf(trackId.toString())

        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.ALBUM_ID
        )

        val cursor = resolver.query(
            uri,
            projection,
            selection,
            selectionArgs,
            null
        )

        cursor?.use { c ->
            if (c.moveToFirst()) {
                val idColumn = c.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
                val titleColumn = c.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
                val artistColumn = c.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
                val albumColumn = c.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
                val durationColumn = c.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
                val albumIdColumn = c.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)

                val id = c.getLong(idColumn)
                val title = c.getString(titleColumn) ?: ""
                val artist = c.getString(artistColumn) ?: ""
                val album = c.getString(albumColumn) ?: ""
                val duration = c.getLong(durationColumn)
                val albumId = c.getLong(albumIdColumn)

                val contentUri = ContentUris.withAppendedId(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    id
                ).toString()

                val coverUri = ContentUris.withAppendedId(
                    "content://media/external/audio/albumart".toUri(),
                    albumId
                ).toString()

                return TrackInfo(
                    id = id,
                    title = title,
                    artist = artist,
                    album = album,
                    duration = duration,
                    uri = contentUri,
                    cover = coverUri
                )
            }
        }
        return null
    }

    ///////////////////////////////////////////////
    // Delete track from device storage
    ///////////////////////////////////////////////
    override fun deleteTrack(
        trackId: Long,
        activity: Activity,
        launcher: ActivityResultLauncher<IntentSenderRequest>
    ) : Boolean {
        val uri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, trackId)
        val context = activity.applicationContext

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val deleteRequest = MediaStore.createDeleteRequest(context.contentResolver, listOf(uri))
            launcher.launch(IntentSenderRequest.Builder(deleteRequest).build())
            return false
        } else {
            try {
                val rows = context.contentResolver.delete(uri, null, null)
                return rows > 0
            } catch (e: SecurityException) {
                val intentSender = when {
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> {
                        (e as? RecoverableSecurityException)?.userAction?.actionIntent?.intentSender
                    }
                    else -> null
                }

                intentSender?.let { sender ->
                    launcher.launch(IntentSenderRequest.Builder(sender).build())
                }
                return false
            }
        }
    }
}

private fun getSortOrder(
    primary: OrderMediaQueue,
    secondary: OrderMediaQueue,
    isAsc: Boolean
): String {
    fun OrderMediaQueue.toSql(): String {
        return when (this) {
            OrderMediaQueue.ALBUM -> MediaStore.Audio.Media.ALBUM
            OrderMediaQueue.ARTIST -> MediaStore.Audio.Media.ARTIST
            OrderMediaQueue.TITLE -> MediaStore.Audio.Media.TITLE
            OrderMediaQueue.DURATION -> MediaStore.Audio.Media.DURATION
            OrderMediaQueue.ID -> MediaStore.Audio.Media._ID
            OrderMediaQueue.TRACK -> MediaStore.Audio.Media.TRACK
            else -> MediaStore.Audio.Media.TRACK
        }
    }

    val asc = if (isAsc) "ASC" else "DESC"
    if (secondary == OrderMediaQueue.ALBUM)
        return "${primary.toSql()} $asc, ${secondary.toSql()} $asc, ${MediaStore.Audio.Media.TRACK} $asc"
    return "${primary.toSql()} $asc, ${secondary.toSql()} $asc"
}
