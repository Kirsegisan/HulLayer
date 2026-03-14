package com.kirsegisan.hullayer.data.datastore

import com.kirsegisan.hullayer.data.values.TrackInfo



data class PreferenceList(
    val trackPrimaryOrder: OrderMediaQueue = OrderMediaQueue.ALBUM,
    val trackSecondaryOrder: OrderMediaQueue = OrderMediaQueue.ID,
    val trackSortAscending: Boolean = true,
    val currentTrack: TrackInfo? = null,
    val isSavedState: Boolean = false,
    val isShuffleMode: Boolean = false
)
