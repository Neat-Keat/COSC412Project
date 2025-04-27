//NewGameScanScreen.kt
package com.example.syncshot.ui.newgame

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.common.util.concurrent.ListenableFuture

@Composable
fun NewGameScanScreen() {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // Permission state
    // ie does the app already have camera permission?
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    // Permission launcher
    // If we dont have permission, request it!
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            hasCameraPermission = granted
        }
    )

    // Request permission
    LaunchedEffect(key1 = true) {
        if (!hasCameraPermission) {
            launcher.launch(Manifest.permission.CAMERA)
        }
    }

    // Camera setup (moved outside of CameraPreview)
    val cameraProviderFuture: ListenableFuture<ProcessCameraProvider> = remember {
        ProcessCameraProvider.getInstance(context)
    }
    val cameraProvider = cameraProviderFuture.get()
    val preview = Preview.Builder().build()
    val previewView = remember { PreviewView(context) }
    val cameraSelector = CameraSelector.Builder()
        .requireLensFacing(CameraSelector.LENS_FACING_BACK)
        .build()

    preview.setSurfaceProvider(previewView.surfaceProvider)

    Scaffold(modifier = Modifier.padding(16.dp)) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            if (hasCameraPermission) {
                Text(
                    text = "Camera Preview",
                    modifier = Modifier.padding(bottom = 8.dp),
                    style = MaterialTheme.typography.headlineSmall
                )
                CameraPreview(cameraProvider, lifecycleOwner, cameraSelector, preview, previewView)
            } else {
                Text(
                    text = "Camera Permission Required",
                    modifier = Modifier.padding(bottom = 8.dp),
                    style = MaterialTheme.typography.headlineSmall
                )
            }
        }
    }
}

@Composable
fun CameraPreview(
    cameraProvider: ProcessCameraProvider,
    lifecycleOwner: androidx.lifecycle.LifecycleOwner,
    cameraSelector: CameraSelector,
    preview: Preview,
    previewView: PreviewView
) {
    // Bind camera to lifecycle only when permission is granted
    LaunchedEffect(key1 = cameraProvider, key2 = lifecycleOwner) {
        try {
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                preview
            )
        } catch (e: Exception) {
            Log.e("CameraPreview", "Error starting camera: ", e)
        }
    }

    AndroidView({ previewView }, modifier = Modifier.fillMaxSize())
}