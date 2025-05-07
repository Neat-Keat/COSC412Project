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
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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

    // Collect StateFlows from the ViewModel as Compose State
    val hasCameraPermission by viewModel.hasCameraPermission.collectAsState()
    val scanStatus by viewModel.scanStatus.collectAsState()

    // Collect the scanned data StateFlows
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
            // Optionally clear tempPhotoUri after use if you don't need to re-process
            // tempPhotoUri = null
        }
    )

    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            uri?.let { viewModel.processSelectedImage(it) }
        }
    )

    // Request camera permission on launch if not granted
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
                // Camera Preview Section
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

                // Action Buttons
                Button(
                    onClick = {
                        // Create a temporary URI to save the photo
                        val photoUri = createTempImageUri(context)
                        tempPhotoUri = photoUri // Store the URI so you can process it later
                        takePictureLauncher.launch(photoUri)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Take Photo")
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = { pickImageLauncher.launch("image/*") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Pick from Gallery")
                }

                // Scan Status Text
                if (!scanStatus.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(scanStatus ?: "", style = MaterialTheme.typography.bodyMedium)
                }

                // OCR Preview Card - Shown when scanStatus indicates completion AND players are found
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

                            // Display Player Names and Strokes
                            // Iterate over the collected player names state
                            playerNamesState.forEachIndexed { i, name ->
                                Text(name, style = MaterialTheme.typography.bodyLarge) // Display the player name
                                Row(modifier = Modifier.fillMaxWidth()) {
                                    // Use getOrNull(i) for safety in case strokesState is not yet populated
                                    // Iterate over strokes for this player (up to 18 holes)
                                    val playerStrokes = strokesState.getOrNull(i)
                                    repeat(18) { holeIndex ->
                                        val stroke = playerStrokes?.getOrNull(holeIndex) ?: -1
                                        Text(
                                            text = if (stroke != -1) stroke.toString() else "-", // Display "-" for missing scores
                                            style = MaterialTheme.typography.bodySmall,
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                            }

                            // Display Par values
                            Text("Par:", style = MaterialTheme.typography.bodyLarge)
                            Row(modifier = Modifier.fillMaxWidth()) {
                                // Iterate over the collected par state (up to 18 holes)
                                repeat(18) { holeIndex ->
                                    val parValue = parState.getOrNull(holeIndex) ?: -1
                                    Text(
                                        text = if (parValue != -1) parValue.toString() else "-", // Display "-" for missing par
                                        style = MaterialTheme.typography.bodySmall,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Edit Scores Button - Navigate using the collected state
                            Button(
                                onClick = {
                                    // Use the collected state for navigation arguments
                                    // Ensure gameDate and gameLocation are not null before navigating if required
                                    // You might want to pass the scanned player data as well if the scores screen needs it
                                    navController.navigate(
                                        Routes.newGameScoresRoute(
                                            numberOfPlayersState, // Use the collected state for number of players
                                            viewModel.gameDate ?: "", // Assuming gameDate is set elsewhere or is nullable
                                            viewModel.gameLocation ?: "" // Assuming gameLocation is set elsewhere or is nullable
                                        )
                                    )
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Edit Scores") // <-- Add the Button content (e.g., Text)
                            }
                        }
                    }
                }
                else if (scanStatus?.contains("complete", ignoreCase = true) == true && playerNamesState.isEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Scan complete, but no players found.", style = MaterialTheme.typography.bodyMedium)
                }

            }
        }
    }

}
private fun createTempImageUri(context: Context): Uri {
    val tempFile = File.createTempFile(
        "temp_image_${System.currentTimeMillis()}", // Prefix and timestamp
        ".jpg", // File extension
        context.cacheDir // Use the app's cache directory
    ).apply {
        createNewFile()
        deleteOnExit() // Delete the file when the app exits
    }

    // Use FileProvider to get a content URI
    // Replace "com.example.syncshot.fileprovider" with your actual FileProvider authority
    return FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider", // Your FileProvider authority (replace with your package name)
        tempFile
    )
}
