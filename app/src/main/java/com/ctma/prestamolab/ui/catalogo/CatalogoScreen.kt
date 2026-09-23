package com.ctma.prestamolab.ui.catalogo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.ctma.prestamolab.model.CategoriaEquipo
import com.ctma.prestamolab.model.Equipo
import com.ctma.prestamolab.model.EstadoEquipo

/**
 * PB-01 / TC-01: catálogo de equipos. Desde la Semana 6 incluye:
 *  - Filtro por categoría (chips), persistido en DataStore.
 *  - Indicador de carga mientras Room responde la primera vez
 *    (cargandoInicial), en vez de mostrar una lista vacía engañosa.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogoScreen(
    equipos: List<Equipo>,
    categoriaFiltro: CategoriaEquipo?,
    cargandoInicial: Boolean = false,
    onCambiarFiltro: (CategoriaEquipo?) -> Unit = {},
    onEquipoClick: (Int) -> Unit,
    onVerMisSolicitudes: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("PréstamoLab CTMA") },
                actions = {
                    IconButton(onClick = onVerMisSolicitudes) {
                        Icon(
                            imageVector = Icons.Filled.List,
                            contentDescription = "Ver mis solicitudes"
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            Text(
                text = "Toca un equipo para ver su detalle o solicitarlo.",
                modifier = Modifier.padding(16.dp)
            )

            FiltroCategorias(
                seleccionada = categoriaFiltro,
                onSeleccionar = onCambiarFiltro
            )

            when {
                cargandoInicial -> {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator()
                        Text(text = "Cargando catálogo...", modifier = Modifier.padding(top = 12.dp))
                    }
                }
                equipos.isEmpty() -> {
                    Text(
                        text = "No hay equipos para esta categoría.",
                        modifier = Modifier.padding(16.dp)
                    )
                }
                else -> {
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
    }
}

@Composable
private fun FiltroCategorias(
    seleccionada: CategoriaEquipo?,
    onSeleccionar: (CategoriaEquipo?) -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            FilterChip(
                selected = seleccionada == null,
                onClick = { onSeleccionar(null) },
                label = { Text("Todas") }
            )
        }
        items(CategoriaEquipo.entries.toList()) { categoria ->
            FilterChip(
                selected = seleccionada == categoria,
                onClick = { onSeleccionar(if (seleccionada == categoria) null else categoria) },
                label = { Text(categoria.name) }
            )
        }
    }
}

@Composable
private fun EquipoCard(equipo: Equipo, onClick: () -> Unit) {
    val estadoTexto = textoEstado(equipo.estado)
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .semantics {
                contentDescription = "${equipo.nombre}, categoría ${equipo.categoria.name}, estado $estadoTexto"
            },
        colors = CardDefaults.cardColors(),
        onClick = onClick
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = equipo.nombre)
            Text(text = "Categoría: ${equipo.categoria.name}")
            Text(text = "Estado: $estadoTexto")
        }
    }
}

private fun textoEstado(estado: EstadoEquipo): String = when (estado) {
    EstadoEquipo.DISPONIBLE -> "Disponible"
    EstadoEquipo.RESERVADO -> "Reservado"
    EstadoEquipo.PRESTADO -> "Prestado"
}
