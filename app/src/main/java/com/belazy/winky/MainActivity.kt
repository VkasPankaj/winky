package com.belazy.winky

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import com.belazy.winky.ui.screens.AppNavGraph
import com.belazy.winky.ui.screens.MainScreen
import com.belazy.winky.ui.theme.GalleryAppTheme
import com.belazy.winky.utils.PermissionsHelper
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val permissionsLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        // Permissions handled automatically by Composables or ViewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Ask for permissions
        if (!PermissionsHelper.hasStoragePermission(this)) {
            permissionsLauncher.launch(PermissionsHelper.getRequiredPermissions())
        }

        setContent {
            GalleryAppTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    AppNavGraph()
                }
            }
        }
    }
}
