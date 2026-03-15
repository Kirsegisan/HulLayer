package com.kirsegisan.hullayer.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.kirsegisan.hullayer.data.values.TrackInfo
import com.kirsegisan.hullayer.ui.layer.ButtonRow
import com.kirsegisan.hullayer.ui.layer.TrackCover
import com.kirsegisan.hullayer.ui.layer.TrackInfoView

@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    Column(
        modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        TrackCover(TrackInfo.EMPTY.uri, Modifier.fillMaxWidth())
        TrackInfoView(Modifier, TrackInfo.EMPTY)
        var pressed by remember { mutableStateOf(false) }
        ButtonRow(
            modifier = Modifier,
            pausePlay = { pressed = !pressed },
            pausePlayIcon = if (pressed) Icons.Default.PlayArrow else Icons.Default.Pause,
            nextTrack = { },
            previewTrack = { }
        )
    }
}