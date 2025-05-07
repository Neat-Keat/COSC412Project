// app/src/main/java/com/example/syncshot/ui/newgamescan/NewGameScanScreen.kt
package com.example.syncshot.ui.newgamescan

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.syncshot.ocr.TesseractHelper
import com.example.syncshot.ui.newgame.NewGameViewModel
import com.example.syncshot.ui.newgamescores.NewGameViewModelFactory
import com.google.common.util.concurrent.ListenableFuture

@Composable
fun NewGameScanScreen(
    navController: NavController
) {
    val context = LocalContext.current

    // 1) Grab your VM via the factory so you can call onImageCaptured(...)
    val vm: NewGameViewModel = viewModel(
        factory = NewGameViewModelFactory(context)
    )

    // 2) Camera permission state
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }
    val permLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted -> hasCameraPermission = granted }

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) permLauncher.launch(Manifest.permission.CAMERA)
    }

    // 3) CameraX preview setup (unchanged)
    val cameraProviderFuture: ListenableFuture<ProcessCameraProvider> =
        remember { ProcessCameraProvider.getInstance(context) }
    val cameraProvider = cameraProviderFuture.get()
    val preview = Preview.Builder().build()
    val previewView = remember { PreviewView(context) }
    val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    preview.setSurfaceProvider(previewView.surfaceProvider)

    // 4) Launcher to take a snapshot, hand it to VM, then navigate
    val takePicLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        bitmap?.let {
            // Send the image to your VM — OCR + insert happens there
            vm.onImageCaptured(it)
            // Then move on to your standard "New Game" setup screen
            navController.navigate("newgame")
        }
    }

    // 5) UI: preview + button (all else exactly as before)
    Scaffold { inner ->
        Column(
            modifier = Modifier
                .padding(inner)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Top
        ) {
            if (hasCameraPermission) {
                Text("Camera Preview", style = MaterialTheme.typography.headlineSmall)
                Spacer(Modifier.height(8.dp))

                CameraPreview(cameraProvider, cameraSelector, preview, previewView)

                Spacer(Modifier.height(12.dp))
                Button(onClick = { takePicLauncher.launch(null) }) {
                    Text("Capture & OCR")
                }
            } else {
                Text(
                    "Camera permission required",
                    style = MaterialTheme.typography.headlineSmall
                )
            }
        }
    }
}

@Composable
private fun CameraPreview(
    cameraProvider: ProcessCameraProvider,
    cameraSelector: CameraSelector,
    preview: Preview,
    previewView: PreviewView
) {
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(cameraProvider, lifecycleOwner) {
        try {
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                preview
            )
        } catch (e: Exception) {
            Log.e("CameraPreview", "Failed to bind camera", e)
        }
    }

    AndroidView(
        factory = { previewView },
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
    )
}
