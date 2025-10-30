package com.belazy.winky.ui.screens

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.core.net.toUri
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.belazy.winky.ui.screens.detail.MediaDetailScreen
import com.belazy.winky.ui.components.VideoPlayer
import com.belazy.winky.ui.screens.folderview.FolderDetailScreen

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "main") {
        composable("main") {
            MainScreen(navController = navController)
        }

        composable(
            "detail/{uri}",
            arguments = listOf(navArgument("uri") { type = NavType.StringType })
        ) { backStackEntry ->
            val encodedUri = backStackEntry.arguments?.getString("uri") ?: return@composable
            val uri = Uri.decode(encodedUri)
            MediaDetailScreen(clickedMediaUri = uri, navController = navController)
        }

        composable("video_detail/{uri}") { backStackEntry ->
            val encodedUri = backStackEntry.arguments?.getString("uri")
            val uri = Uri.decode(encodedUri)
            VideoPlayer(uri = uri.toUri())
        }
        composable("folderDetail/{folderName}") { backStackEntry ->
            val folderName = backStackEntry.arguments?.getString("folderName") ?: ""
            FolderDetailScreen(navController, folderName)
        }

    }
}
