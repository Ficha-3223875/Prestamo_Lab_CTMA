package com.ctma.prestamolab.ui.misprestamos

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.ctma.prestamolab.model.EstadoSolicitud
import com.ctma.prestamolab.model.SolicitudPrestamo

/**
 * PB-08/PB-09/PB-10 + HU-07: detalle y GESTIÓN completa de una solicitud
 * (no solo cancelar). Los botones visibles dependen del estado actual,
 * siguiendo estrictamente las transiciones RN-07/RN-10/RN-11/RN-12:
 *
 * SOLICITADA -> (Aprobar -> APROBADA) | (Rechazar -> RECHAZADA) | (Cancelar -> CANCELADA)
 * APROBADA   -> (Entregar -> ENTREGADA)
 * ENTREGADA  -> (Devolver -> DEVUELTA)
 *
 * Un solicitudId inexistente (RN-08) muestra un mensaje recuperable en vez
 * de fallar.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SolicitudDetalleScreen(
    solicitud: SolicitudPrestamo?,
    onAprobar: (Int) -> Unit,
    onRechazar: (Int) -> Unit,
    onEntregar: (Int) -> Unit,
    onDevolver: (Int) -> Unit,
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

            // HU-10: cada bloque de texto describe el dato que representa
            // para que un lector de pantalla lo anuncie con contexto, no
            // solo el valor suelto.
            Text(
                text = "Solicitud #${solicitud.id}",
                modifier = Modifier.semantics { contentDescription = "Solicitud número ${solicitud.id}" }
            )
            Text(text = "Equipo ID: ${solicitud.equipoId}")
            Text(text = "Destino: ${solicitud.ambienteDestino}")
            Text(text = "Propósito: ${solicitud.proposito}")
            Text(text = "Duración: ${solicitud.duracionHoras} horas")
            Text(
                text = "Estado: ${solicitud.estado.name}",
                modifier = Modifier.semantics { contentDescription = "Estado actual: ${solicitud.estado.name}" }
            )

            when (solicitud.estado) {
                EstadoSolicitud.SOLICITADA -> {
                    Button(
                        onClick = { onAprobar(solicitud.id) },
                        modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
                    ) { Text("Aprobar solicitud") }
                    OutlinedButton(
                        onClick = { onRechazar(solicitud.id) },
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                    ) { Text("Rechazar solicitud") }
                    OutlinedButton(
                        onClick = { onCancelar(solicitud.id) },
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                    ) { Text("Cancelar solicitud") }
                }
                EstadoSolicitud.APROBADA -> {
                    Button(
                        onClick = { onEntregar(solicitud.id) },
                        modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
                    ) { Text("Marcar como entregada") }
                }
                EstadoSolicitud.ENTREGADA -> {
                    Button(
                        onClick = { onDevolver(solicitud.id) },
                        modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
                    ) { Text("Registrar devolución") }
                }
                EstadoSolicitud.DEVUELTA,
                EstadoSolicitud.CANCELADA,
                EstadoSolicitud.RECHAZADA -> {
                    Text(
                        text = "Esta solicitud ya está en un estado final (${solicitud.estado.name}) y no admite más acciones.",
                        modifier = Modifier.padding(top = 16.dp)
                    )
                }
            }
        }
    }
}
