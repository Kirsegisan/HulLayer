package com.kirsegisan.hullayer.data.values

data class TrackInfo(
    val id: Long,
    val title: String,
    val artist: String,
    val album: String,
    val duration: Long,
    val uri: String,
    val cover: String = "none",
) {
    companion object {
        val EMPTY = TrackInfo(
            id = -1,
            title = "",
            artist = "",
            album = "",
            duration = 0L,
            uri = "",
        )
    }
}
