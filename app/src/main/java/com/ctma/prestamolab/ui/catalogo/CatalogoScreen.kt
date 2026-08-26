package com.ctma.prestamolab.ui.catalogo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ctma.prestamolab.model.Equipo
import com.ctma.prestamolab.model.EstadoEquipo

/**
 * PB-01 / TC-01: catálogo de equipos con nombre, categoría y disponibilidad.
 * La disponibilidad nunca se comunica solo con color (criterio de
 * accesibilidad 10.5): siempre va acompañada de texto ("Disponible",
 * "Reservado", "Prestado").
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogoScreen(
    equipos: List<Equipo>,
    onEquipoClick: (Int) -> Unit,
    onVerMisSolicitudes: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("PréstamoLab CTMA") },
                actions = {
                    IconButton(onClick = onVerMisSolicitudes) { Icon( imageVector = Icons.Filled.List, contentDescription = "Ver mis solicitudes" ) }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            Text(
                text = "Toca un equipo para ver su detalle o solicitarlo.",
                modifier = Modifier.padding(16.dp)
            )
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(equipos, key = { it.id }) { equipo ->
                    EquipoCard(equipo = equipo, onClick = { onEquipoClick(equipo.id) })
                }
            }
        }
    }
}

@Composable
private fun EquipoCard(equipo: Equipo, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(),
        onClick = onClick
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = equipo.nombre)
            Text(text = "Categoría: ${equipo.categoria.name}")
            Text(text = "Estado: ${textoEstado(equipo.estado)}")
        }
    }
}

private fun textoEstado(estado: EstadoEquipo): String = when (estado) {
    EstadoEquipo.DISPONIBLE -> "Disponible"
    EstadoEquipo.RESERVADO -> "Reservado"
    EstadoEquipo.PRESTADO -> "Prestado"
}
