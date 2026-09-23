package com.ctma.prestamolab

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.ctma.prestamolab.data.local.AppDatabase
import com.ctma.prestamolab.data.preferences.FiltrosPreferences
import com.ctma.prestamolab.data.repository.RoomPrestamoRepository
import com.ctma.prestamolab.navigation.PrestamoNavGraph
import com.ctma.prestamolab.ui.theme.PrestamoLabTheme
import com.ctma.prestamolab.viewmodel.PrestamoViewModel
import com.ctma.prestamolab.viewmodel.PrestamoViewModelFactory

class MainActivity : ComponentActivity() {

    // Semana 6: Room reemplaza al Repository en memoria como fuente
    // real de datos de la app. La base de datos vive mientras viva
    // el proceso (patrón singleton dentro de AppDatabase). La
    // siembra del catálogo ocurre sola, dentro del Repository, la
    // primera vez que se consulta — no hace falta dispararla aquí.
    private val database by lazy { AppDatabase.obtenerInstancia(applicationContext) }
    private val repository by lazy { RoomPrestamoRepository(database.equipoDao(), database.solicitudDao()) }
    private val preferencias by lazy { FiltrosPreferences(applicationContext) }

    private val viewModel: PrestamoViewModel by viewModels {
        PrestamoViewModelFactory(repository, preferencias)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PrestamoLabTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    PrestamoNavGraph(viewModel = viewModel)
                }
            }
        }
    }
}
