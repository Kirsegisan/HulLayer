package com.kirsegisan.hullayer.ui.screens


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kirsegisan.hullayer.ui.layer.TrackCover
import com.kirsegisan.hullayer.ui.layer.TrackInfoView
import com.kirsegisan.hullayer.ui.viewmodel.PlaylistViewModel


@Composable
fun PlaylistScreen(
    modifier: Modifier,
    viewModel: PlaylistViewModel = viewModel(factory = PlaylistViewModel.Factory)
){
    val trackList by viewModel.trackList.collectAsState()
    LazyColumn(
        modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ){
        items(trackList, key = {it.id}){item ->
            Row(
                Modifier.fillMaxWidth()
            ) {
                TrackCover(
                    item.cover,
                    Modifier
                        .size(58.dp)
                        .fillMaxWidth()
                        .padding(5.dp)
                        .requiredSize(60.dp)
                        .aspectRatio(1f)
                        .clip(shape = MaterialTheme.shapes.small)
                )
                TrackInfoView(
                    Modifier
                        .fillMaxWidth(),
                    trackInfo = item
                )
            }
        }
    }
}
