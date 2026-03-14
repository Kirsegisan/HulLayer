package com.kirsegisan.hullayer.ui.layer

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest

@Composable
fun TrackCover(cover: String?, modifier: Modifier = Modifier) {
    Box(modifier = modifier) {
        SubcomposeAsyncImage(
            model = ImageRequest
                .Builder(LocalContext.current)
                .data(cover)
                .crossfade(true)
                .build(),
            contentDescription = "Album Art",
            modifier = Modifier
                .padding(end = 16.dp)
                .requiredSize(48.dp)
                .aspectRatio(1f)
                .clip(shape = MaterialTheme.shapes.small),
            contentScale = ContentScale.Crop,
            error = {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.DarkGray),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        imageVector = Icons.Default.MusicNote,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        )
    }
}

@Preview
@Composable
fun CoverPreview() {
    TrackCover(cover = null)
}