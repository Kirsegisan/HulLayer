package com.kirsegisan.hullayer.ui.viewmodel

import com.kirsegisan.hullayer.data.values.TrackInfo

data class PlayerUiState(
    val trackInfo: TrackInfo = TrackInfo.EMPTY,
    val isPlaying: Boolean = false,
)
