// Updated NewGameScanScreen.kt
package com.example.syncshot.ui.newgamescan

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.syncshot.ui.newgame.NewGameViewModel
import com.example.syncshot.ui.newgamescores.NewGameViewModelFactory
import com.example.syncshot.ui.nav.Routes
import java.io.File

@Composable
fun NewGameScanScreen(
    navController: NavController,
    viewModel: NewGameViewModel = viewModel(factory = NewGameViewModelFactory(LocalContext.current))
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val hasCameraPermission by viewModel.hasCameraPermission.collectAsState()
    val scanStatus by viewModel.scanStatus.collectAsState()
    val playerNamesState by viewModel.playerNames.collectAsState()
    val strokesState by viewModel.strokes.collectAsState()
    val parState by viewModel.par.collectAsState()
    val numberOfPlayersState by viewModel.numberOfPlayers.collectAsState()

    var tempPhotoUri by remember { mutableStateOf<Uri?>(null) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted -> viewModel.setCameraPermissionStatus(granted) }
    )

    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->
            if (success) {
                tempPhotoUri?.let { viewModel.processSelectedImage(it) }
            }
        }
    )

    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            uri?.let {
                tempPhotoUri = it
                viewModel.processSelectedImage(it)
            }
        }
    )

    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        } else {
            viewModel.setCameraPermissionStatus(true)
        }
    }

    Scaffold(modifier = Modifier.padding(16.dp)) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            Text(
                text = "Scan Scorecard",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            if (hasCameraPermission) {
                val previewView = remember { PreviewView(context) }

                AndroidView(
                    factory = { previewView },
                    modifier = Modifier
                        .weight(1f)
                        .padding(bottom = 16.dp)
                )

                LaunchedEffect(Unit) {
                    try {
                        val cameraProvider = ProcessCameraProvider.getInstance(context).get()
                        val preview = Preview.Builder().build().apply {
                            setSurfaceProvider(previewView.surfaceProvider)
                        }
                        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
                        cameraProvider.unbindAll()
                        cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, preview)
                    } catch (e: Exception) {
                        Log.e("CameraPreview", "Error starting camera: ", e)
                    }
                }

                Button(
                    onClick = {
                        val photoUri = createTempImageUri(context)
                        tempPhotoUri = photoUri
                        viewModel.processSelectedImage(photoUri)
                        takePictureLauncher.launch(photoUri)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Take Photo")
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        tempPhotoUri = null
                        viewModel.setCameraPermissionStatus(true)
                        pickImageLauncher.launch("image/*")
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Pick from Gallery")
                }

                tempPhotoUri?.let { uri ->
                    Spacer(modifier = Modifier.height(12.dp))
                    Image(
                        painter = rememberAsyncImagePainter(uri),
                        contentDescription = "Selected Image",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .padding(bottom = 8.dp)
                    )
                }

                if (!scanStatus.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(scanStatus ?: "", style = MaterialTheme.typography.bodyMedium)
                }

                if (scanStatus?.contains("complete", ignoreCase = true) == true && playerNamesState.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Scanned Preview:", style = MaterialTheme.typography.titleMedium)
                            Spacer(modifier = Modifier.height(8.dp))

                            playerNamesState.forEachIndexed { i, name ->
                                Text(name, style = MaterialTheme.typography.bodyLarge)
                                Row(modifier = Modifier.fillMaxWidth()) {
                                    val playerStrokes = strokesState.getOrNull(i)
                                    repeat(18) { holeIndex ->
                                        val stroke = playerStrokes?.getOrNull(holeIndex) ?: -1
                                        Text(
                                            text = if (stroke != -1) stroke.toString() else "-",
                                            style = MaterialTheme.typography.bodySmall,
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                            }

                            Text("Par:", style = MaterialTheme.typography.bodyLarge)
                            Row(modifier = Modifier.fillMaxWidth()) {
                                repeat(18) { holeIndex ->
                                    val parValue = parState.getOrNull(holeIndex) ?: -1
                                    Text(
                                        text = if (parValue != -1) parValue.toString() else "-",
                                        style = MaterialTheme.typography.bodySmall,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Button(
                                onClick = {
                                    navController.navigate(
                                        Routes.newGameScoresRoute(
                                            numberOfPlayersState,
                                            viewModel.gameDate ?: "",
                                            viewModel.gameLocation ?: ""
                                        )
                                    )
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Edit Scores")
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Button(
                                onClick = {
                                    viewModel.insertGame()
                                    navController.navigate(Routes.GameList) {
                                        popUpTo(Routes.GameList) { inclusive = true }
                                    }
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Save Now")
                            }
                        }
                    }
                } else if (scanStatus?.contains("complete", ignoreCase = true) == true && playerNamesState.isEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Scan complete, but no players found.", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

private fun createTempImageUri(context: Context): Uri {
    val tempFile = File.createTempFile(
        "temp_image_${System.currentTimeMillis()}",
        ".jpg",
        context.cacheDir
    ).apply {
        createNewFile()
        deleteOnExit()
    }

    return FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        tempFile
    )
}