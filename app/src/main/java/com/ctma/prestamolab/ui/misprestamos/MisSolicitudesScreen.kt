package com.ctma.prestamolab.ui.misprestamos

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.ctma.prestamolab.model.SolicitudPrestamo

/**
 * PB-07: lista "Mis solicitudes". Si está vacía, se explica por qué en
 * lugar de dejar una pantalla en blanco (buena práctica de accesibilidad
 * y de usabilidad, aunque no está numerada como regla en la guía).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MisSolicitudesScreen(
    solicitudes: List<SolicitudPrestamo>,
    onSolicitudClick: (Int) -> Unit,
    onVolverCatalogo: () -> Unit
) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Mis solicitudes") }) }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (solicitudes.isEmpty()) {
                Text(
                    text = "Todavía no has registrado solicitudes de préstamo.",
                    modifier = Modifier.padding(16.dp)
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(solicitudes, key = { it.id }) { solicitud ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .semantics {
                                    contentDescription = "Solicitud número ${solicitud.id}, " +
                                        "estado ${solicitud.estado.name}, " +
                                        "destino ${solicitud.ambienteDestino}"
                                },
                            onClick = { onSolicitudClick(solicitud.id) }
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(text = "Solicitud #${solicitud.id}")
                                Text(text = "Estado: ${solicitud.estado.name}")
                                Text(text = "Destino: ${solicitud.ambienteDestino}")
                            }
                        }
                    }
                }
            }
        }
    }
}
