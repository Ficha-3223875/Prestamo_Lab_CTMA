package com.example.prestamo_lab_ctma.ui.solicitud

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.prestamo_lab_ctma.model.Equipo
import com.example.prestamo_lab_ctma.model.EstadoSolicitud
import com.example.prestamo_lab_ctma.model.SolicitudPrestamo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SolicitudDetalleScreen(
    solicitud: SolicitudPrestamo?,
    equipo: Equipo?,
    onCancelar: (Int) -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle de Solicitud") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (solicitud == null) {
                Text("Error: Solicitud no encontrada.")
            } else {
                Text(text = "Solicitud #${solicitud.id}", style = MaterialTheme.typography.headlineSmall)
                Text(text = "Equipo: ${equipo?.nombre ?: "Cargando..."}")
                Text(text = "Estado: ${solicitud.estado}", style = MaterialTheme.typography.titleMedium)
                Divider()
                Text(text = "Ambiente/Destino: ${solicitud.ambienteDestino}")
                Text(text = "Propósito: ${solicitud.proposito}")
                Text(text = "Duración: ${solicitud.duracionHoras} horas")
                
                Spacer(modifier = Modifier.weight(1f))
                
                // RN-07: Solo una solicitud SOLICITADA puede cancelarse
                if (solicitud.estado == EstadoSolicitud.SOLICITADA) {
                    OutlinedButton(
                        onClick = { onCancelar(solicitud.id) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text("Cancelar solicitud")
                    }
                }
            }
        }
    }
}
