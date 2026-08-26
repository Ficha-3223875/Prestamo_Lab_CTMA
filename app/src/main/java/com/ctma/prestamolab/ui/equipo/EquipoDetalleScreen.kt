package com.ctma.prestamolab.ui.equipo

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
import com.ctma.prestamolab.model.Equipo
import com.ctma.prestamolab.model.EstadoEquipo

/**
 * PB-02 / TC-02 / TC-03: detalle de un equipo recibido por equipoId.
 * Si el equipoId no existe (RN-08), se muestra un mensaje recuperable en
 * lugar de cerrar la app abruptamente (ver NavGraph, que resuelve el
 * equipo antes de llegar aquí).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EquipoDetalleScreen(
    equipo: Equipo?,
    onSolicitar: (Int) -> Unit,
    onVolver: () -> Unit
) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Detalle del equipo") }) }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            if (equipo == null) {
                Text("Este equipo ya no existe o el identificador no es válido.")
                Button(onClick = onVolver, modifier = Modifier.padding(top = 12.dp)) {
                    Text("Volver al catálogo")
                }
            } else {
                Text(text = equipo.nombre)
                Text(text = "Categoría: ${equipo.categoria.name}")
                Text(text = "Estado: ${equipo.estado.name}")
                Button(
                    onClick = { onSolicitar(equipo.id) },
                    enabled = equipo.estado == EstadoEquipo.DISPONIBLE,
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    Text(if (equipo.estado == EstadoEquipo.DISPONIBLE) "Solicitar préstamo" else "No disponible")
                }
            }
        }
    }
}
