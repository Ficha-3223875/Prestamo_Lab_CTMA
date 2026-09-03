package com.example.prestamo_lab_ctma.ui.misprestamos

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.prestamo_lab_ctma.model.Equipo
import com.example.prestamo_lab_ctma.model.SolicitudPrestamo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MisSolicitudesScreen(
    solicitudes: List<SolicitudPrestamo>,
    equipos: List<Equipo>,
    onSolicitudClick: (Int) -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Mis Solicitudes") })
        }
    ) { padding ->
        if (solicitudes.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = androidx.compose.ui.Alignment.Center) {
                Text("Aún no tienes solicitudes.")
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(solicitudes) { solicitud ->
                    val equipo = equipos.find { it.id == solicitud.equipoId }
                    SolicitudItem(
                        solicitud = solicitud,
                        equipoNombre = equipo?.nombre ?: "Equipo desconocido",
                        onClick = { onSolicitudClick(solicitud.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun SolicitudItem(solicitud: SolicitudPrestamo, equipoNombre: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Solicitud #${solicitud.id}", style = MaterialTheme.typography.titleMedium)
            Text(text = "Equipo: $equipoNombre")
            Text(text = "Estado: ${solicitud.estado}")
            Text(text = "Destino: ${solicitud.ambienteDestino}")
        }
    }
}
