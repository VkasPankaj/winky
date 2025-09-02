// ui/screens/NavGraph.kt
package com.belazy.winky.ui.screens

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.belazy.winky.ui.screens.detail.MediaDetailScreen
import androidx.core.net.toUri
import com.belazy.winky.ui.components.VideoPlayer

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "main") {
        composable("main") {
            MainScreen(navController = navController)
        }

        composable(
            "detail/{index}/{type}",
            arguments = listOf(
                navArgument("index") { type = NavType.IntType },
                navArgument("type") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val index = backStackEntry.arguments?.getInt("index") ?: 0
            val type = backStackEntry.arguments?.getString("type") ?: "image"
            MediaDetailScreen(  index, type = type, navController = navController)
        }

        composable("video_detail/{uri}") { backStackEntry ->
            val encodedUri = backStackEntry.arguments?.getString("uri")
            val uri = Uri.decode(encodedUri)
            VideoPlayer(uri = uri.toUri())
        }


    }
}
