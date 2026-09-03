package com.example.prestamo_lab_ctma.ui.equipo

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.prestamo_lab_ctma.model.Equipo
import com.example.prestamo_lab_ctma.model.EstadoEquipo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EquipoDetalleScreen(
    equipo: Equipo?,
    onBack: () -> Unit,
    onSolicitar: (Int) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle del Equipo") },
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
                .fillMaxSize()
        ) {
            if (equipo == null) {
                // RN-08: Manejo de IDs inexistentes
                Text("El equipo solicitado no existe.")
                Button(onClick = onBack) { Text("Volver al catálogo") }
            } else {
                Text(text = equipo.nombre, style = MaterialTheme.typography.headlineMedium)
                Text(text = "Categoría: ${equipo.categoria}", style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "Estado: ${equipo.estado}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (equipo.estado == EstadoEquipo.DISPONIBLE) MaterialTheme.colorScheme.primary 
                            else MaterialTheme.colorScheme.error
                )
                
                Spacer(modifier = Modifier.weight(1f))
                
                // RN-01: Solo se puede solicitar si está DISPONIBLE
                Button(
                    onClick = { onSolicitar(equipo.id) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = equipo.estado == EstadoEquipo.DISPONIBLE
                ) {
                    Text("Solicitar préstamo")
                }
            }
        }
    }
}
