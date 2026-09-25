package com.ctma.prestamolab.ui.misprestamos

import android.Manifest
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ctma.prestamolab.data.notifications.NotificacionesHelper
import com.ctma.prestamolab.data.sensors.SensorLuz
import com.ctma.prestamolab.model.EstadoEvidencia
import com.ctma.prestamolab.model.EstadoSolicitud
import com.ctma.prestamolab.model.EvidenciaFoto
import com.ctma.prestamolab.model.SolicitudPrestamo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

/**
 * Detalle y gestión de una solicitud. Desde la Semana 9 agrega:
 *  - Sección de evidencia fotográfica (Photo Picker + estados de
 *    sincronización) cuando la solicitud está ENTREGADA o DEVUELTA.
 *  - Lectura del sensor de luz ambiental como aviso antes de elegir
 *    la foto (capacidad física adicional, actividad 31).
 *  - Solicitud del permiso de notificaciones justo antes de registrar
 *    una devolución — no al abrir la app (actividad 33).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SolicitudDetalleScreen(
    solicitud: SolicitudPrestamo?,
    evidencias: Flow<List<EvidenciaFoto>> = emptyFlow(),
    onAprobar: (Int) -> Unit,
    onRechazar: (Int) -> Unit,
    onEntregar: (Int) -> Unit,
    onDevolver: (Int) -> Unit,
    onCancelar: (Int) -> Unit,
    onAgregarEvidencia: (Int, String, Float?) -> Unit = { _, _, _ -> },
    onSincronizarEvidencia: (Int) -> Unit = {},
    onVolver: () -> Unit
) {
    val contexto = LocalContext.current
    val listaEvidencias by evidencias.collectAsStateWithLifecycle(initialValue = emptyList())

    val sensorLuz = remember { SensorLuz(contexto) }
    val luxActual by sensorLuz.observarLux().collectAsStateWithLifecycle(initialValue = null)

    val selectorFoto = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null && solicitud != null) {
            onAgregarEvidencia(solicitud.id, uri.toString(), luxActual)
        }
    }

    val lanzadorPermisoNotificaciones = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { /* Concedido o no, se continúa igual: la notificación es un plus, no bloquea la devolución. */ }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Detalle de solicitud") }) }
    ) { padding ->
        Column(modifier = Modifier.fillMaxWidth().padding(padding).padding(16.dp)) {
            if (solicitud == null) {
                Text("Esta solicitud ya no existe o el identificador no es válido.")
                Button(onClick = onVolver, modifier = Modifier.padding(top = 12.dp)) { Text("Volver") }
                return@Scaffold
            }

            Text(text = "Solicitud #${solicitud.id}")
            Text(text = "Equipo ID: ${solicitud.equipoId}")
            Text(text = "Destino: ${solicitud.ambienteDestino}")
            Text(text = "Propósito: ${solicitud.proposito}")
            Text(text = "Duración: ${solicitud.duracionHoras} horas")
            Text(text = "Estado: ${solicitud.estado.name}")

            when (solicitud.estado) {
                EstadoSolicitud.SOLICITADA -> {
                    Button(onClick = { onAprobar(solicitud.id) }, modifier = Modifier.fillMaxWidth().padding(top = 16.dp)) { Text("Aprobar solicitud") }
                    OutlinedButton(onClick = { onRechazar(solicitud.id) }, modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) { Text("Rechazar solicitud") }
                    OutlinedButton(onClick = { onCancelar(solicitud.id) }, modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) { Text("Cancelar solicitud") }
                }
                EstadoSolicitud.APROBADA -> {
                    Button(onClick = { onEntregar(solicitud.id) }, modifier = Modifier.fillMaxWidth().padding(top = 16.dp)) { Text("Marcar como entregada") }
                }
                EstadoSolicitud.ENTREGADA -> {
                    Button(
                        onClick = {
                            // Pide el permiso justo aquí, antes de la acción que lo
                            // necesita (mostrar la notificación) — mínimo privilegio.
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                val yaConcedido = ContextCompat.checkSelfPermission(
                                    contexto, Manifest.permission.POST_NOTIFICATIONS
                                ) == android.content.pm.PackageManager.PERMISSION_GRANTED
                                if (!yaConcedido) {
                                    lanzadorPermisoNotificaciones.launch(Manifest.permission.POST_NOTIFICATIONS)
                                }
                            }
                            onDevolver(solicitud.id)
                        },
                        modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
                    ) { Text("Registrar devolución") }
                }
                EstadoSolicitud.DEVUELTA, EstadoSolicitud.CANCELADA, EstadoSolicitud.RECHAZADA -> {
                    Text(
                        text = "Esta solicitud ya está en un estado final (${solicitud.estado.name}) y no admite más acciones.",
                        modifier = Modifier.padding(top = 16.dp)
                    )
                }
            }

            // Sección de evidencia: disponible una vez el equipo fue entregado.
            if (solicitud.estado == EstadoSolicitud.ENTREGADA || solicitud.estado == EstadoSolicitud.DEVUELTA) {
                Text(text = "Evidencia fotográfica", modifier = Modifier.padding(top = 24.dp))

                val textoLuz = when {
                    !sensorLuz.disponible -> "Sensor de luz no disponible en este dispositivo."
                    luxActual == null -> "Leyendo sensor de luz..."
                    luxActual!! < SensorLuz.UMBRAL_LUZ_BAJA_LUX -> "⚠ Poca luz ambiental (${luxActual!!.toInt()} lux). La foto podría no ser clara."
                    else -> "Luz ambiental: ${luxActual!!.toInt()} lux."
                }
                Text(text = textoLuz, modifier = Modifier.padding(top = 4.dp))

                Button(
                    onClick = {
                        selectorFoto.launch(
                            androidx.activity.result.PickVisualMediaRequest(
                                ActivityResultContracts.PickVisualMedia.ImageOnly
                            )
                        )
                    },
                    modifier = Modifier.padding(top = 8.dp)
                ) { Text("Agregar foto de evidencia") }

                if (listaEvidencias.isEmpty()) {
                    Text(text = "Sin evidencia registrada todavía.", modifier = Modifier.padding(top = 8.dp))
                } else {
                    LazyColumn(modifier = Modifier.padding(top = 8.dp)) {
                        items(listaEvidencias, key = { it.id }) { evidencia ->
                            TarjetaEvidencia(evidencia = evidencia, onSincronizar = { onSincronizarEvidencia(evidencia.id) })
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TarjetaEvidencia(evidencia: EvidenciaFoto, onSincronizar: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = "Estado: ${textoEstadoEvidencia(evidencia.estado)}")
            if (evidencia.luxAlCapturar != null) {
                Text(text = "Luz al capturar: ${evidencia.luxAlCapturar.toInt()} lux")
            }
            if (evidencia.estado == EstadoEvidencia.LOCAL || evidencia.estado == EstadoEvidencia.FALLIDA) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 4.dp)) {
                    OutlinedButton(onClick = onSincronizar) { Text("Sincronizar") }
                }
            }
        }
    }
}

private fun textoEstadoEvidencia(estado: EstadoEvidencia): String = when (estado) {
    EstadoEvidencia.LOCAL -> "Guardada localmente"
    EstadoEvidencia.SUBIENDO -> "Subiendo..."
    EstadoEvidencia.SINCRONIZADA -> "Sincronizada"
    EstadoEvidencia.FALLIDA -> "Falló la sincronización (se conserva localmente)"
}
