package com.kirsegisan.hullayer.ui.layer

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


@Composable
fun NavigationBottom(
    modifier: Modifier = Modifier,
    onNavigate: () -> Unit = {},
) {
    Box(modifier = Modifier.size(
        width = 400.dp,
        height = 60.dp
    )) {
        IconButton(
            onClick = onNavigate,
            modifier = Modifier.align(Alignment.TopEnd)
        ) {
            Icon(
                imageVector = Icons.Default.FormatListNumbered,
                contentDescription = "navigation",
                modifier = Modifier.size(60.dp)
            )
        }
    }
}