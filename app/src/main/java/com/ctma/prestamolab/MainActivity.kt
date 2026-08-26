package com.ctma.prestamolab

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.ctma.prestamolab.data.repository.InMemoryPrestamoRepository
import com.ctma.prestamolab.navigation.PrestamoNavGraph
import com.ctma.prestamolab.ui.theme.PrestamoLabTheme
import com.ctma.prestamolab.viewmodel.PrestamoViewModel
import com.ctma.prestamolab.viewmodel.PrestamoViewModelFactory

class MainActivity : ComponentActivity() {

    // Repository InMemory único y compartido durante la ejecución de la
    // Activity (punto 11 del alcance mínimo, sección 4.3).
    private val repository = InMemoryPrestamoRepository()

    private val viewModel: PrestamoViewModel by viewModels {
        PrestamoViewModelFactory(repository)
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
