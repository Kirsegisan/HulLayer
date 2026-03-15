package com.kirsegisan.hullayer.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.kirsegisan.hullayer.ui.screens.PlaylistScreen
import com.kirsegisan.hullayer.ui.screens.PlayerScreen

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    Scaffold(modifier) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = NavRoute.PlayerScreen.name,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(route = NavRoute.PlayerScreen.name) {
                PlayerScreen(
                    onNavigateToPlaylist = { navController.navigate(NavRoute.Playlist.name) },
                    modifier = Modifier.fillMaxSize()
                )
            }
            composable(route = NavRoute.Playlist.name) {
                PlaylistScreen(modifier = Modifier.fillMaxSize())
            }
        }
    }
}