package com.belazy.winky.ui.screens.deleted

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter

@Composable
fun DeletedScreen(deletedItems: List<Uri>, onRestore: (Uri) -> Unit) {
    LazyVerticalGrid(columns = GridCells.Fixed(3), modifier = Modifier.fillMaxSize()) {
        items(deletedItems) { uri ->
            Column {
                Image(
                    painter = rememberAsyncImagePainter(uri),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(2.dp)
                        .size(100.dp)
                )
                Button(onClick = { onRestore(uri) }) {
                    Text("Undo")
                }
            }
        }
    }
}
