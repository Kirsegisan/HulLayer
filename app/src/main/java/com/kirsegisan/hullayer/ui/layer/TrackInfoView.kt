package com.kirsegisan.hullayer.ui.layer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kirsegisan.hullayer.data.values.TrackInfo


@Composable
fun TrackInfoView(
    modifier: Modifier = Modifier,
    trackInfo: TrackInfo
) {
    val name = trackInfo.title.ifEmpty { "name" }
    val artist = trackInfo.artist.ifEmpty { "artist" }
    val album = trackInfo.album.ifEmpty { "album" }
    val duration = trackInfo.duration

    Card {
        Row(
            modifier = modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = name,
                    modifier = Modifier.padding(bottom = 4.dp),
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Start,
                    softWrap = true,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                )

                Text(
                    text = "$artist - $album",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Start,
                    softWrap = true,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }
        }
    }
}

@Preview
@Composable
fun PreviewInfo(){
    TrackInfoView(
        modifier = Modifier.fillMaxWidth(),
        TrackInfo(
        id = 1,
        title = "",
        artist = "",
        album = "",
        duration = 1000,
        uri = "uri",
    ))
}