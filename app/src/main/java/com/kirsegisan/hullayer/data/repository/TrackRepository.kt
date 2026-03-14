package com.kirsegisan.hullayer.data.repository

import android.app.Activity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import com.kirsegisan.hullayer.data.values.TrackInfo
import kotlinx.coroutines.flow.Flow

interface TrackRepository {
    fun getAllTracks(): Flow<List<TrackInfo>>
    fun deleteTrack(trackId: Long, activity: Activity, launcher: ActivityResultLauncher<IntentSenderRequest>) : Boolean
    suspend fun getTracksPaged(page: Int, pageSize: Int): List<TrackInfo>
    suspend fun getTrackById(trackId: Int): TrackInfo?
}
