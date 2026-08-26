package com.ctma.prestamolab.ui.solicitud

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ctma.prestamolab.model.Equipo

/**
 * PB-03/PB-04/PB-05/PB-06: formulario de solicitud.
 * La validación se hace en el ViewModel/dominio (10.4), no aquí: esta
 * pantalla solo representa el UiState y emite el evento onGuardar.
 * "guardando" deshabilita el botón para frenar la doble pulsación (BUG-03)
 * ya desde la UI, además del guard en el ViewModel y el Repository.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SolicitarScreen(
    equipo: Equipo?,
    guardando: Boolean,
    mensaje: String?,
    onGuardar: (ambienteDestino: String, proposito: String, duracionHoras: Int) -> Unit,
    onVolver: () -> Unit
) {
    var ambienteDestino by remember { mutableStateOf("") }
    var proposito by remember { mutableStateOf("") }
    var duracionTexto by remember { mutableStateOf("") }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Solicitar préstamo") }) }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            if (equipo == null) {
                Text("El equipo seleccionado ya no está disponible.")
                Button(onClick = onVolver, modifier = Modifier.padding(top = 12.dp)) {
                    Text("Volver")
                }
                return@Scaffold
            }

            Text(text = "Equipo: ${equipo.nombre}")

            OutlinedTextField(
                value = ambienteDestino,
                onValueChange = { ambienteDestino = it },
                label = { Text("Ambiente o destino") },
                modifier = Modifier.fillMaxWidth().padding(top = 12.dp)
            )
            OutlinedTextField(
                value = proposito,
                onValueChange = { proposito = it },
                label = { Text("Propósito (10 a 180 caracteres)") },
                modifier = Modifier.fillMaxWidth().padding(top = 12.dp)
            )
            OutlinedTextField(
                value = duracionTexto,
                onValueChange = { nuevo -> if (nuevo.all { it.isDigit() }) duracionTexto = nuevo },
                label = { Text("Duración estimada en horas (1 a 8)") },
                modifier = Modifier.fillMaxWidth().padding(top = 12.dp)
            )

            if (mensaje != null) {
                Text(text = mensaje, modifier = Modifier.padding(top = 12.dp))
            }

            Button(
                onClick = {
                    val horas = duracionTexto.toIntOrNull() ?: -1
                    onGuardar(ambienteDestino, proposito, horas)
                },
                enabled = !guardando,
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text(if (guardando) "Guardando..." else "Guardar")
            }
        }
    }
}
