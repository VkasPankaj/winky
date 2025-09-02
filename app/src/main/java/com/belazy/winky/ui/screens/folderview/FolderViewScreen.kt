package com.belazy.winky.ui.screens.folderview

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.belazy.winky.data.repository.FolderRepository

@Composable
fun FolderViewScreen(navController: NavController) {
    val context = LocalContext.current
    val folders = remember { mutableStateListOf<com.belazy.winky.data.model.Folder>() }

    LaunchedEffect(true) {
        folders.clear()
        folders.addAll(FolderRepository.getFolders(context))
    }

    LazyColumn(modifier = Modifier.fillMaxSize().padding(8.dp)) {
        items(folders) { folder ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp)
                    .clickable {
                        // Navigate to folder media screen
                    }
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = folder.name, style = MaterialTheme.typography.titleMedium)
                    Text(text = "${folder.mediaCount} items", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}
