package com.ctma.prestamolab.ui.misprestamos

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ctma.prestamolab.model.EstadoSolicitud
import com.ctma.prestamolab.model.SolicitudPrestamo

/**
 * PB-08/PB-09/PB-10: detalle de una solicitud, con opción de cancelar
 * cuando el estado es SOLICITADA (RN-07) y manejo de un solicitudId
 * inexistente sin cierre abrupto (RN-08, TC-16 caso negativo).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SolicitudDetalleScreen(
    solicitud: SolicitudPrestamo?,
    onCancelar: (Int) -> Unit,
    onVolver: () -> Unit
) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Detalle de solicitud") }) }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            if (solicitud == null) {
                Text("Esta solicitud ya no existe o el identificador no es válido.")
                Button(onClick = onVolver, modifier = Modifier.padding(top = 12.dp)) {
                    Text("Volver")
                }
                return@Scaffold
            }

            Text(text = "Solicitud #${solicitud.id}")
            Text(text = "Equipo ID: ${solicitud.equipoId}")
            Text(text = "Destino: ${solicitud.ambienteDestino}")
            Text(text = "Propósito: ${solicitud.proposito}")
            Text(text = "Duración: ${solicitud.duracionHoras} horas")
            Text(text = "Estado: ${solicitud.estado.name}")

            Button(
                onClick = { onCancelar(solicitud.id) },
                enabled = solicitud.estado == EstadoSolicitud.SOLICITADA,
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text(
                    if (solicitud.estado == EstadoSolicitud.SOLICITADA)
                        "Cancelar solicitud"
                    else
                        "No se puede cancelar (${solicitud.estado.name})"
                )
            }
        }
    }
}
