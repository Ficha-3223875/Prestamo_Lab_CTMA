package com.example.prestamo_lab_ctma.ui.solicitud

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.prestamo_lab_ctma.model.Equipo
import com.example.prestamo_lab_ctma.model.EstadoSolicitud
import com.example.prestamo_lab_ctma.model.SolicitudPrestamo
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SolicitudDetalleScreen(
    solicitud: SolicitudPrestamo?,
    equipo: Equipo?,
    onCancelar: (Int) -> Unit,
    onBack: () -> Unit
) {
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle de Solicitud") },
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
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (solicitud == null) {
                Text("Error: Solicitud no encontrada.")
            } else {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "Solicitud #${solicitud.id}",
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            text = "Equipo: ${equipo?.nombre ?: "Cargando..."}",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(text = "Estado:", style = MaterialTheme.typography.bodyMedium)
                            AssistChip(
                                onClick = {},
                                label = { Text(solicitud.estado.name) },
                                colors = AssistChipDefaults.assistChipColors(
                                    containerColor = when (solicitud.estado) {
                                        EstadoSolicitud.SOLICITADA -> MaterialTheme.colorScheme.primaryContainer
                                        EstadoSolicitud.CANCELADA -> MaterialTheme.colorScheme.errorContainer
                                        else -> MaterialTheme.colorScheme.secondaryContainer
                                    }
                                )
                            )
                        }
                    }
                }

                HorizontalDivider()

                Text(text = "Información del Préstamo", style = MaterialTheme.typography.titleMedium)
                Text(text = "• Ambiente / Destino: ${solicitud.ambienteDestino}")
                Text(text = "• Propósito: ${solicitud.proposito}")
                Text(text = "• Duración estimada: ${solicitud.duracionHoras} horas")

                if (!solicitud.photoPath.isNullOrBlank()) {
                    HorizontalDivider()
                    Text(text = "Evidencia Fotográfica:", style = MaterialTheme.typography.titleMedium)
                    Image(
                        painter = rememberAsyncImagePainter(File(solicitud.photoPath)),
                        contentDescription = "Foto de evidencia del equipo",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // RN-07: Solo una solicitud SOLICITADA puede cancelarse
                if (solicitud.estado == EstadoSolicitud.SOLICITADA) {
                    Button(
                        onClick = { onCancelar(solicitud.id) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text("Cancelar Solicitud")
                    }
                } else if (solicitud.estado == EstadoSolicitud.CANCELADA) {
                    OutlinedButton(
                        onClick = {},
                        enabled = false,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Solicitud Cancelada")
                    }
                }
            }
        }
    }
}
