package com.example.prestamo_lab_ctma.ui.solicitud

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import coil.compose.rememberAsyncImagePainter
import com.example.prestamo_lab_ctma.model.Equipo
import com.example.prestamo_lab_ctma.ui.viewmodel.FormularioSolicitudState
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SolicitarPrestamoScreen(
    equipo: Equipo?,
    formState: FormularioSolicitudState,
    guardando: Boolean,
    onAmbienteChange: (String) -> Unit,
    onPropositoChange: (String) -> Unit,
    onDuracionChange: (String) -> Unit,
    onCapturePhoto: () -> Unit,
    onPhotoSelected: (String) -> Unit,
    onGuardar: () -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            onCapturePhoto()
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            Log.d("SolicitarPrestamo", "Selected URI: $uri")
            val cachedPath = copyUriToCache(context, uri)
            if (cachedPath != null) {
                onPhotoSelected(cachedPath)
            } else {
                Log.e("SolicitarPrestamo", "Failed to cache URI: $uri")
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Solicitar Préstamo") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (equipo == null) {
                Text("Error: Equipo no encontrado.")
            } else {
                Text("Solicitando: ${equipo.nombre}", style = MaterialTheme.typography.titleLarge)
                
                OutlinedTextField(
                    value = formState.ambienteDestino,
                    onValueChange = onAmbienteChange,
                    label = { Text("Ambiente o destino") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = formState.errorAmbiente != null,
                    supportingText = { formState.errorAmbiente?.let { Text(it) } }
                )
                
                OutlinedTextField(
                    value = formState.proposito,
                    onValueChange = onPropositoChange,
                    label = { Text("Propósito") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = formState.errorProposito != null,
                    supportingText = { formState.errorProposito?.let { Text(it) } },
                    minLines = 3
                )
                
                OutlinedTextField(
                    value = formState.duracionHoras,
                    onValueChange = onDuracionChange,
                    label = { Text("Duración en horas (1-8)") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = formState.errorDuracion != null,
                    supportingText = { formState.errorDuracion?.let { Text(it) } },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                Text("Evidencia Fotográfica:", style = MaterialTheme.typography.titleMedium)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            val permissionCheckResult = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                            if (permissionCheckResult == PackageManager.PERMISSION_GRANTED) {
                                onCapturePhoto()
                            } else {
                                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                            }
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                    ) {
                        Text("📷 Tomar Foto")
                    }

                    OutlinedButton(
                        onClick = {
                            galleryLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("🖼️ Galería")
                    }
                }

                if (!formState.photoPath.isNullOrBlank()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = rememberAsyncImagePainter(File(formState.photoPath)),
                                contentDescription = "Vista previa de evidencia",
                                modifier = Modifier
                                    .size(80.dp)
                                    .clip(RoundedCornerShape(8.dp)),
                                contentScale = ContentScale.Crop
                            )
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Evidencia Seleccionada ✓",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "Foto adjuntada correctamente",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                            IconButton(onClick = { onPhotoSelected("") }) {
                                Icon(Icons.Default.Clear, contentDescription = "Eliminar evidencia")
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Button(
                    onClick = onGuardar,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = formState.puedeGuardar && !guardando
                ) {
                    if (guardando) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Guardar solicitud")
                    }
                }
            }
        }
    }
}

private fun copyUriToCache(context: Context, uri: Uri): String? {
    return try {
        Log.d("SolicitarPrestamo", "Copying URI to cache: $uri")
        if (uri.scheme == "file" && uri.path != null) {
            return uri.path
        }
        val inputStream = context.contentResolver.openInputStream(uri)
        if (inputStream == null) {
            Log.e("SolicitarPrestamo", "Failed to open input stream for $uri")
            return null
        }
        val file = File(context.cacheDir, "evidencia_${System.currentTimeMillis()}.jpg")
        file.outputStream().use { outputStream ->
            inputStream.use { input ->
                input.copyTo(outputStream)
            }
        }
        Log.d("SolicitarPrestamo", "Successfully cached file at: ${file.absolutePath}")
        file.absolutePath
    } catch (e: Exception) {
        Log.e("SolicitarPrestamo", "Error copying URI to cache", e)
        null
    }
}
