package com.belazy.winky.ui.screens.folderview

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.belazy.winky.data.model.Folder
import com.belazy.winky.ui.screens.detail.GalleryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FolderViewScreen(
    navController: NavController,
    viewModel: GalleryViewModel = hiltViewModel()
) {
    val folders by viewModel.folders.collectAsState()

    // 🔥 Load everything in background as soon as screen opens
    LaunchedEffect(Unit) {
        viewModel.loadAllMediaInBackground()
    }

    Scaffold(
        containerColor = Color.Black
    ) { paddingValues ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.Black)
                .padding(horizontal = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(vertical = 12.dp)
        ) {
            items(folders) { folder ->
                FolderCard(folder = folder) {
                    navController.navigate("folderDetail/${folder.name}")
                }
            }
        }
    }
}

@Composable
fun FolderCard(folder: Folder, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .clickable { onClick() }
            .width(IntrinsicSize.Min),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier.aspectRatio(1f),
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            if (!folder.coverImagePath.isNullOrEmpty()) {
                AsyncImage(
                    model = folder.coverImagePath,
                    contentDescription = "${folder.name} cover",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = androidx.compose.ui.layout.ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = folder.name,
            style = MaterialTheme.typography.titleMedium.copy(fontSize = 16.sp),
            color = Color.White,
            maxLines = 1
        )
        Text(
            text = "${folder.mediaCount} ${if (folder.mediaCount == 1) "item" else "items"}",
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
            color = Color.White
        )
    }
}