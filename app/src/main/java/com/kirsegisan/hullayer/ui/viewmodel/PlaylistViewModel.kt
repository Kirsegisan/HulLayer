package com.kirsegisan.hullayer.ui.viewmodel

import android.app.Application
import android.content.ComponentName
import androidx.annotation.OptIn
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.kirsegisan.hullayer.data.HulLayerApplication
import com.kirsegisan.hullayer.data.repository.TrackRepository
import com.kirsegisan.hullayer.domain.PlaybackService
import com.kirsegisan.hullayer.domain.toMediaItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@OptIn(UnstableApi::class)
class PlaylistViewModel(
    application: Application,
    private val trackRepository: TrackRepository
) : AndroidViewModel(application) {
    private val _uiState = MutableStateFlow(PlayerUiState())
    private var mediaController: MediaController? = null
    private val playerListener = object : Player.Listener {
        override fun onEvents(player: Player, events: Player.Events) {
            super.onEvents(player, events)
            if (events.containsAny(Player.EVENT_PLAYBACK_STATE_CHANGED, Player.EVENT_IS_PLAYING_CHANGED)) {
                _uiState.value = _uiState.value.copy(
                    isPlaying = player.isPlaying
                )
            }
            if (events.containsAny(Player.EVENT_MEDIA_ITEM_TRANSITION, Player.EVENT_MEDIA_METADATA_CHANGED)) {
                updateCurrentTrack(player)
            }
        }
    }
    val uiState = _uiState.asStateFlow()

    init {
        val sessionToken = SessionToken(
            getApplication(),
            ComponentName(getApplication(), PlaybackService::class.java)
        )

        val controllerFuture = MediaController.Builder(getApplication(), sessionToken).buildAsync()
        controllerFuture.addListener(
            {
                mediaController = controllerFuture.get()
                mediaController?.addListener(playerListener)
                viewModelScope.launch {
                    trackRepository.getAllTracks().collect { tracks ->
                        if (tracks.isEmpty()) return@collect
                        val controller = mediaController ?: return@collect
                        val mediaItems = tracks.map { it.toMediaItem() }

                        if (controller.mediaItemCount == 0) {
                            controller.setMediaItems(mediaItems)
                            controller.prepare()
                        }
                    }
                }
            },
            { it.run() }
        )
    }

    fun togglePlay(){
        val controller = mediaController ?: return
        if (controller.isPlaying) controller.pause() else controller.play()
    }
    fun skipToNext() = mediaController?.seekToNext()
    fun skipToPrevious() = mediaController?.seekToPrevious()

    private fun updateCurrentTrack(p: Player){
        viewModelScope.launch {
            val current = p.currentMediaItemIndex
            trackRepository.getAllTracks().collect { trackInfos ->
                if (current in trackInfos.indices) {
                    _uiState.value = _uiState.value.copy(
                        trackInfo = trackInfos[current]
                    )
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        mediaController?.removeListener(playerListener)
        mediaController?.release()
        mediaController = null
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as HulLayerApplication)
                val trackRepository = application.container.trackRepository
                PlaylistViewModel(
                    application = application,
                    trackRepository = trackRepository
                )
            }
        }
    }
}
