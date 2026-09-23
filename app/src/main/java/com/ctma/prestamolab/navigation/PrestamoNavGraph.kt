package com.ctma.prestamolab.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.ctma.prestamolab.ui.catalogo.CatalogoScreen
import com.ctma.prestamolab.ui.equipo.EquipoDetalleScreen
import com.ctma.prestamolab.ui.misprestamos.MisSolicitudesScreen
import com.ctma.prestamolab.ui.misprestamos.SolicitudDetalleScreen
import com.ctma.prestamolab.ui.solicitud.SolicitarScreen
import com.ctma.prestamolab.viewmodel.PrestamoViewModel

object PrestamoDestinos {
    const val CATALOGO = "catalogo"
    const val EQUIPO_DETALLE = "equipoDetalle/{equipoId}"
    const val SOLICITAR = "solicitar/{equipoId}"
    const val MIS_SOLICITUDES = "misSolicitudes"
    const val SOLICITUD_DETALLE = "solicitudDetalle/{solicitudId}"

    fun equipoDetalle(equipoId: Int) = "equipoDetalle/$equipoId"
    fun solicitar(equipoId: Int) = "solicitar/$equipoId"
    fun solicitudDetalle(solicitudId: Int) = "solicitudDetalle/$solicitudId"
}

@Composable
fun PrestamoNavGraph(
    viewModel: PrestamoViewModel,
    navController: NavHostController = rememberNavController()
) {
    val uiState by viewModel.uiState.collectAsState()

    NavHost(navController = navController, startDestination = PrestamoDestinos.CATALOGO) {

        composable(PrestamoDestinos.CATALOGO) {
            CatalogoScreen(
                equipos = uiState.equiposFiltrados,
                categoriaFiltro = uiState.categoriaFiltro,
                cargandoInicial = uiState.cargandoInicial,
                onCambiarFiltro = { categoria -> viewModel.cambiarFiltroCategoria(categoria) },
                onEquipoClick = { id -> navController.navigate(PrestamoDestinos.equipoDetalle(id)) },
                onVerMisSolicitudes = { navController.navigate(PrestamoDestinos.MIS_SOLICITUDES) }
            )
        }

        composable(
            route = PrestamoDestinos.EQUIPO_DETALLE,
            arguments = listOf(navArgument("equipoId") { type = NavType.IntType })
        ) { backStackEntry ->
            val equipoId = backStackEntry.arguments?.getInt("equipoId") ?: -1
            val equipo = viewModel.obtenerEquipo(equipoId)
            EquipoDetalleScreen(
                equipo = equipo,
                onSolicitar = { id -> navController.navigate(PrestamoDestinos.solicitar(id)) },
                onVolver = { navController.popBackStack() }
            )
        }

        composable(
            route = PrestamoDestinos.SOLICITAR,
            arguments = listOf(navArgument("equipoId") { type = NavType.IntType })
        ) { backStackEntry ->
            val equipoId = backStackEntry.arguments?.getInt("equipoId") ?: -1
            val equipo = viewModel.obtenerEquipo(equipoId)
            SolicitarScreen(
                equipo = equipo,
                guardando = uiState.guardando,
                mensaje = uiState.mensaje,
                onGuardar = { ambienteDestino, proposito, duracionHoras ->
                    viewModel.crearSolicitud(
                        equipoId = equipoId,
                        ambienteDestino = ambienteDestino,
                        proposito = proposito,
                        duracionHoras = duracionHoras,
                        onExito = {
                            navController.navigate(PrestamoDestinos.MIS_SOLICITUDES) {
                                popUpTo(PrestamoDestinos.CATALOGO)
                            }
                        }
                    )
                },
                onVolver = { navController.popBackStack() }
            )
        }

        composable(PrestamoDestinos.MIS_SOLICITUDES) {
            MisSolicitudesScreen(
                solicitudes = uiState.solicitudes,
                onSolicitudClick = { id -> navController.navigate(PrestamoDestinos.solicitudDetalle(id)) },
                onVolverCatalogo = { navController.popBackStack(PrestamoDestinos.CATALOGO, inclusive = false) }
            )
        }

        composable(
            route = PrestamoDestinos.SOLICITUD_DETALLE,
            arguments = listOf(navArgument("solicitudId") { type = NavType.IntType })
        ) { backStackEntry ->
            val solicitudId = backStackEntry.arguments?.getInt("solicitudId") ?: -1
            val solicitud = viewModel.obtenerSolicitud(solicitudId)
            SolicitudDetalleScreen(
                solicitud = solicitud,
                onAprobar = { id -> viewModel.aprobarSolicitud(id) },
                onRechazar = { id -> viewModel.rechazarSolicitud(id) },
                onEntregar = { id -> viewModel.entregarSolicitud(id) },
                onDevolver = { id -> viewModel.devolverSolicitud(id) },
                onCancelar = { id -> viewModel.cancelarSolicitud(id) },
                onVolver = { navController.popBackStack() }
            )
        }
    }
}
