package com.example.prestamo_lab_ctma.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.compose.ui.platform.LocalContext
import com.example.prestamo_lab_ctma.data.local.AppDatabase
import com.example.prestamo_lab_ctma.data.local.UserPreferencesRepository
import com.example.prestamo_lab_ctma.data.repository.LocalPrestamoRepository
import com.example.prestamo_lab_ctma.ui.catalogo.CatalogoScreen
import com.example.prestamo_lab_ctma.ui.equipo.EquipoDetalleScreen
import com.example.prestamo_lab_ctma.ui.misprestamos.MisSolicitudesScreen
import com.example.prestamo_lab_ctma.ui.solicitud.SolicitarPrestamoScreen
import com.example.prestamo_lab_ctma.ui.solicitud.SolicitudDetalleScreen
import com.example.prestamo_lab_ctma.ui.viewmodel.PrestamoViewModel
import com.example.prestamo_lab_ctma.ui.viewmodel.PrestamoViewModelFactory

sealed class Screen(val route: String) {
    object Catalogo : Screen("catalogo")
    object EquipoDetalle : Screen("equipo/{equipoId}") {
        fun createRoute(id: Int) = "equipo/$id"
    }
    object Solicitar : Screen("solicitar/{equipoId}") {
        fun createRoute(id: Int) = "solicitar/$id"
    }
    object MisSolicitudes : Screen("mis_solicitudes")
    object SolicitudDetalle : Screen("solicitud/{solicitudId}") {
        fun createRoute(id: Int) = "solicitud/$id"
    }
    object Camera : Screen("camera")
}

@Composable
fun AppNavigation() {
    val context = LocalContext.current
    val hardwareManager = remember { com.example.prestamo_lab_ctma.ui.util.HardwareManager(context) }
    val database = AppDatabase.getDatabase(context)
    val userPrefs = remember { UserPreferencesRepository(context) }
    
    val viewModel: PrestamoViewModel = viewModel(
        factory = PrestamoViewModelFactory(
            LocalPrestamoRepository(database.equipoDao(), database.solicitudDao()),
            userPrefs
        )
    )
    val navController = rememberNavController()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val formState by viewModel.formularioState.collectAsStateWithLifecycle()

    // RN: Efecto de vibración al completar operaciones
    LaunchedEffect(uiState.operacionExitosa) {
        if (uiState.operacionExitosa) {
            hardwareManager.vibrate()
        }
    }

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                NavigationBarItem(
                    icon = { Icon(Icons.Default.List, contentDescription = null) },
                    label = { Text("Catálogo") },
                    selected = currentRoute == Screen.Catalogo.route,
                    onClick = {
                        navController.navigate(Screen.Catalogo.route) {
                            popUpTo(navController.graph.startDestinationId)
                            launchSingleTop = true
                        }
                    }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Person, contentDescription = null) },
                    label = { Text("Mis Préstamos") },
                    selected = currentRoute == Screen.MisSolicitudes.route,
                    onClick = {
                        navController.navigate(Screen.MisSolicitudes.route) {
                            popUpTo(navController.graph.startDestinationId)
                            launchSingleTop = true
                        }
                    }
                )
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Catalogo.route,
            modifier = Modifier.padding(padding)
        ) {
            composable(Screen.Catalogo.route) {
                CatalogoScreen(
                    equipos = uiState.equipos,
                    isLoading = uiState.isLoading,
                    error = uiState.error,
                    selectedCategory = uiState.filterCategory,
                    onCategorySelected = viewModel::updateFilter,
                    onEquipoClick = { id -> 
                        navController.navigate(Screen.EquipoDetalle.createRoute(id)) 
                    }
                )
            }
            
            composable(
                route = Screen.EquipoDetalle.route,
                arguments = listOf(navArgument("equipoId") { type = NavType.IntType })
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getInt("equipoId") ?: -1
                EquipoDetalleScreen(
                    equipo = viewModel.obtenerEquipoPorId(id),
                    onBack = { navController.popBackStack() },
                    onSolicitar = { equipoId -> 
                        navController.navigate(Screen.Solicitar.createRoute(equipoId)) 
                    }
                )
            }
            
            composable(
                route = Screen.Solicitar.route,
                arguments = listOf(navArgument("equipoId") { type = NavType.IntType })
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getInt("equipoId") ?: -1
                
                LaunchedEffect(uiState.operacionExitosa) {
                    if (uiState.operacionExitosa) {
                        navController.navigate(Screen.MisSolicitudes.route) {
                            popUpTo(Screen.Catalogo.route)
                        }
                        viewModel.limpiarMensaje()
                    }
                }

                SolicitarPrestamoScreen(
                    equipo = viewModel.obtenerEquipoPorId(id),
                    formState = formState,
                    guardando = uiState.guardando,
                    onAmbienteChange = viewModel::onAmbienteChange,
                    onPropositoChange = viewModel::onPropositoChange,
                    onDuracionChange = viewModel::onDuracionChange,
                    onCapturePhoto = { navController.navigate(Screen.Camera.route) },
                    onGuardar = { viewModel.guardarSolicitud(id) },
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Screen.Camera.route) {
                com.example.prestamo_lab_ctma.ui.solicitud.CameraCapture(
                    onImageCaptured = { path ->
                        viewModel.onPhotoCaptured(path)
                        navController.popBackStack()
                    },
                    onBack = { navController.popBackStack() }
                )
            }
            
            composable(Screen.MisSolicitudes.route) {
                MisSolicitudesScreen(
                    solicitudes = uiState.solicitudes,
                    equipos = uiState.equipos,
                    onSolicitudClick = { id -> 
                        navController.navigate(Screen.SolicitudDetalle.createRoute(id)) 
                    },
                    onBack = { navController.popBackStack() }
                )
            }
            
            composable(
                route = Screen.SolicitudDetalle.route,
                arguments = listOf(navArgument("solicitudId") { type = NavType.IntType })
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getInt("solicitudId") ?: -1
                val solicitud = viewModel.obtenerSolicitudPorId(id)
                SolicitudDetalleScreen(
                    solicitud = solicitud,
                    equipo = solicitud?.let { viewModel.obtenerEquipoPorId(it.equipoId) },
                    onCancelar = { viewModel.cancelarSolicitud(id) },
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
