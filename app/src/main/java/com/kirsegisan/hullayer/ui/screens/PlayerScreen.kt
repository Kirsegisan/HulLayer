package com.kirsegisan.hullayer.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kirsegisan.hullayer.ui.layer.ButtonRow
import com.kirsegisan.hullayer.ui.layer.TrackCover
import com.kirsegisan.hullayer.ui.layer.TrackInfoView
import com.kirsegisan.hullayer.ui.viewmodel.PlaylistViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.kirsegisan.hullayer.ui.layer.NavigationBottom

@Composable
fun PlayerScreen(
    onNavigateToPlaylist: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PlaylistViewModel = viewModel(factory = PlaylistViewModel.Factory)
) {
    val isPlaying = viewModel.uiState.collectAsState().value.isPlaying
    val trackInfo = viewModel.uiState.collectAsState().value.trackInfo

    Column(
        modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        NavigationBottom(
            Modifier.fillMaxWidth(),
            onNavigate = onNavigateToPlaylist,
        )
        TrackCover(trackInfo.cover, Modifier
            .size(280.dp)
            .fillMaxWidth()
            .padding(16.dp)
            .requiredSize(300.dp)
            .aspectRatio(1f)
            .clip(shape = MaterialTheme.shapes.small)
        )
        TrackInfoView(Modifier, trackInfo)
        ButtonRow(
            modifier = Modifier.size(
                width = 350.dp,
                height = 100.dp
            ),
            pausePlay = { viewModel.togglePlay() },
            pausePlayIcon = if (!isPlaying) Icons.Default.PlayArrow else Icons.Default.Pause,
            nextTrack = { viewModel.skipToNext() },
            previewTrack = { viewModel.skipToPrevious() },
            onNavigate = onNavigateToPlaylist
        )
    }
}