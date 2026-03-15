package com.kirsegisan.hullayer.ui.layer

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun ButtonRow(
    modifier: Modifier,
    pausePlay: () -> Unit,
    pausePlayIcon: ImageVector,
    nextTrack: () -> Unit,
    previewTrack: () -> Unit,
    onNavigate: () -> Unit = {}
){
    Row(
        modifier = modifier.fillMaxSize()
    ) {
        IconButton(
            onClick = previewTrack,
            modifier
                .weight(1f)
        ) {
            Icon(
                imageVector = Icons.Default.SkipPrevious,
                contentDescription = "SkipPrevious",
                modifier.size(60.dp)
            )
        }
        IconButton(
            onClick = pausePlay,
            modifier.weight(1f)
        ) {
            Icon(
                imageVector = pausePlayIcon,
                contentDescription = "pausePlayIcon",
                modifier.size(60.dp)
            )
        }
        IconButton(
            onClick = nextTrack,
            modifier.weight(1f)
        ) {
            Icon(
                imageVector = Icons.Default.SkipNext,
                contentDescription = "SkipNext",
                modifier.size(60.dp)
            )
        }
    }
}


@Preview
@Composable
fun ButtonRowPreview(){
    var pressed by remember { mutableStateOf(false) }
    ButtonRow(
        modifier = Modifier,
        pausePlay = { pressed = !pressed},
        pausePlayIcon = if (pressed) Icons.Default.PlayArrow else Icons.Default.Pause,
        nextTrack = { },
        previewTrack = { }
    )
}