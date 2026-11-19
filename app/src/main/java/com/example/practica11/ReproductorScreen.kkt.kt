package com.example.practica11

import android.media.MediaPlayer
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.filled.Stop

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReproductorScreen() {
    val context = LocalContext.current

    // Estados del reproductor
    var audioUri by remember { mutableStateOf<Uri?>(null) }
    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }
    var isPlaying by remember { mutableStateOf(false) }

    // Abrir el explorador de archivos
    val selectorAudio = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            audioUri = uri

            // Liberar el reproductor anterior
            mediaPlayer?.release()

            // Crear y reproducir el audio seleccionado
            mediaPlayer = MediaPlayer.create(context, uri)
            mediaPlayer?.start()
            isPlaying = mediaPlayer != null

            // Detectar cuando termina la canción
            mediaPlayer?.setOnCompletionListener {
                isPlaying = false
            }
        }
    }

    // Liberar recursos al cerrar la pantalla
    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer?.release()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mini Reproductor MP3") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = if (audioUri == null)
                    "Ningún archivo seleccionado"
                else
                    "Audio cargado correctamente",
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Buscar archivos de audio
            Button(
                onClick = { selectorAudio.launch("audio/*") }
            ) {
                Text("Buscar MP3 en SD/Memoria")
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Controles de reproducción
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {

                FloatingActionButton(
                    onClick = {
                        if (mediaPlayer != null && !isPlaying) {
                            mediaPlayer?.start()
                            isPlaying = true
                        }
                    }
                ) {
                    Icon(
                        Icons.Default.PlayArrow,
                        contentDescription = "Reproducir"
                    )
                }

                FloatingActionButton(
                    onClick = {
                        if (mediaPlayer != null && isPlaying) {
                            mediaPlayer?.pause()
                            isPlaying = false
                        }
                    }
                ) {
                    Text(
                        "Ⅱ",
                        style = MaterialTheme.typography.titleLarge
                    )
                }

                // Botón para detener la reproducción
                FloatingActionButton(
                    onClick = {
                        if (mediaPlayer != null) {
                            mediaPlayer?.pause()
                            isPlaying = false
                            mediaPlayer?.seekTo(0)
                        }
                    }
                ) {
                    Icon(
                        Icons.Default.Stop,
                        contentDescription = "Detener"
                    )
                }
            }
        }
    }
}