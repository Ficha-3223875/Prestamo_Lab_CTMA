package com.example.prestamo_lab_ctma.data.repository

import com.example.prestamo_lab_ctma.model.Equipo
import com.example.prestamo_lab_ctma.model.SolicitudPrestamo
import kotlinx.coroutines.flow.Flow

interface PrestamoRepository {
    fun obtenerEquipos(): Flow<List<Equipo>>
    suspend fun obtenerEquipo(id: Int): Equipo?
    fun obtenerSolicitudes(): Flow<List<SolicitudPrestamo>>
    suspend fun obtenerSolicitud(id: Int): SolicitudPrestamo?
    suspend fun crearSolicitud(solicitud: SolicitudPrestamo): Result<Unit>
    suspend fun cancelarSolicitud(id: Int): Result<Unit>
    suspend fun inicializarEquipos()
}
