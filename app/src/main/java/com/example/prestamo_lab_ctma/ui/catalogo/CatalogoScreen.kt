package com.example.prestamo_lab_ctma.ui.catalogo

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.prestamo_lab_ctma.model.CategoriaEquipo
import com.example.prestamo_lab_ctma.model.Equipo
import com.example.prestamo_lab_ctma.model.EstadoEquipo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogoScreen(
    equipos: List<Equipo>,
    isLoading: Boolean,
    error: String?,
    selectedCategory: String?,
    onCategorySelected: (String?) -> Unit,
    onEquipoClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Catálogo de Equipos") })
        }
    ) { padding ->
        Column(modifier = modifier.padding(padding)) {
            // Filtros de categoría (Semana 6)
            CategoryFilterRow(
                selectedCategory = selectedCategory,
                onCategorySelected = onCategorySelected
            )

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (error != null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = error, color = MaterialTheme.colorScheme.error)
                }
            } else if (equipos.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No hay equipos disponibles en esta categoría.")
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(equipos) { equipo ->
                        EquipoItem(equipo = equipo, onClick = { onEquipoClick(equipo.id) })
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryFilterRow(
    selectedCategory: String?,
    onCategorySelected: (String?) -> Unit
) {
    LazyRow(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            FilterChip(
                selected = selectedCategory == null,
                onClick = { onCategorySelected(null) },
                label = { Text("Todos") }
            )
        }
        items(CategoriaEquipo.values()) { category ->
            FilterChip(
                selected = selectedCategory == category.name,
                onClick = { onCategorySelected(category.name) },
                label = { Text(category.name) }
            )
        }
    }
}

@Composable
fun EquipoItem(equipo: Equipo, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = equipo.nombre, style = MaterialTheme.typography.titleMedium)
            Text(text = "Categoría: ${equipo.categoria}", style = MaterialTheme.typography.bodyMedium)
            
            // RN-20: Accesibilidad (texto e indicador visual no dependiente solo de color)
            val colorEstado = when (equipo.estado) {
                EstadoEquipo.DISPONIBLE -> Color(0xFF2E7D32)
                EstadoEquipo.RESERVADO -> Color(0xFFEF6C00)
                EstadoEquipo.PRESTADO -> Color(0xFFC62828)
            }
            
            Surface(
                color = colorEstado.copy(alpha = 0.1f),
                contentColor = colorEstado,
                shape = MaterialTheme.shapes.small
            ) {
                Text(
                    text = "Estado: ${equipo.estado}",
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelMedium
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            TextButton(onClick = onClick) {
                Text("Ver detalle")
            }
        }
    }
}
