package com.example.prestamo_lab_ctma.data.repository

import com.example.prestamo_lab_ctma.data.local.dao.EquipoDao
import com.example.prestamo_lab_ctma.data.local.dao.SolicitudDao
import com.example.prestamo_lab_ctma.data.local.entities.EquipoEntity
import com.example.prestamo_lab_ctma.data.local.entities.SolicitudEntity
import com.example.prestamo_lab_ctma.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class LocalPrestamoRepository(
    private val equipoDao: EquipoDao,
    private val solicitudDao: SolicitudDao
) : PrestamoRepository {

    override fun obtenerEquipos(): Flow<List<Equipo>> {
        return equipoDao.obtenerTodos().map { list ->
            list.map { it.toDomain() }
        }
    }

    override suspend fun obtenerEquipo(id: Int): Equipo? {
        return equipoDao.obtenerPorId(id)?.toDomain()
    }

    override fun obtenerSolicitudes(): Flow<List<SolicitudPrestamo>> {
        return solicitudDao.obtenerTodas().map { list ->
            list.map { it.toDomain() }
        }
    }

    override suspend fun obtenerSolicitud(id: Int): SolicitudPrestamo? {
        return solicitudDao.obtenerPorId(id)?.toDomain()
    }

    override suspend fun crearSolicitud(solicitud: SolicitudPrestamo): Result<Unit> {
        return try {
            val equipo = equipoDao.obtenerPorId(solicitud.equipoId)
                ?: return Result.failure(Exception("Equipo no encontrado"))

            if (equipo.estado != EstadoEquipo.DISPONIBLE) {
                return Result.failure(Exception("El equipo no está disponible"))
            }

            // Crear solicitud
            solicitudDao.insertarSolicitud(solicitud.toEntity())

            // Actualizar estado del equipo
            equipoDao.actualizarEquipo(equipo.copy(estado = EstadoEquipo.RESERVADO))

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun cancelarSolicitud(id: Int): Result<Unit> {
        return try {
            val solicitud = solicitudDao.obtenerPorId(id)
                ?: return Result.failure(Exception("Solicitud no encontrada"))

            if (solicitud.estado != EstadoSolicitud.SOLICITADA) {
                return Result.failure(Exception("Solo se pueden cancelar solicitudes en estado SOLICITADA"))
            }

            // Actualizar solicitud
            solicitudDao.actualizarSolicitud(solicitud.copy(estado = EstadoSolicitud.CANCELADA))

            // Liberar equipo
            val equipo = equipoDao.obtenerPorId(solicitud.equipoId)
            if (equipo != null) {
                equipoDao.actualizarEquipo(equipo.copy(estado = EstadoEquipo.DISPONIBLE))
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun inicializarEquipos() {
        val count = equipoDao.obtenerTodos().first().size
        if (count == 0) {
            val iniciales = listOf(
                EquipoEntity(1, "Multímetro digital", CategoriaEquipo.ELECTRONICA, EstadoEquipo.DISPONIBLE),
                EquipoEntity(2, "Kit Arduino", CategoriaEquipo.ELECTRONICA, EstadoEquipo.DISPONIBLE),
                EquipoEntity(3, "Cámara digital", CategoriaEquipo.MULTIMEDIA, EstadoEquipo.DISPONIBLE),
                EquipoEntity(4, "Tablet educativa", CategoriaEquipo.INFORMATICA, EstadoEquipo.RESERVADO),
                EquipoEntity(5, "Taladro inalámbrico", CategoriaEquipo.HERRAMIENTAS, EstadoEquipo.PRESTADO),
                EquipoEntity(6, "Fuente de alimentación", CategoriaEquipo.ELECTRONICA, EstadoEquipo.DISPONIBLE),
                EquipoEntity(7, "Protoboard", CategoriaEquipo.ELECTRONICA, EstadoEquipo.DISPONIBLE),
                EquipoEntity(8, "Cámara web", CategoriaEquipo.MULTIMEDIA, EstadoEquipo.DISPONIBLE)
            )
            equipoDao.insertarEquipos(iniciales)
        }
    }

    // Mappers
    private fun EquipoEntity.toDomain() = Equipo(id, nombre, categoria, estado)
    private fun SolicitudEntity.toDomain() = SolicitudPrestamo(id, equipoId, ambienteDestino, proposito, duracionHoras, estado)
    private fun SolicitudPrestamo.toEntity() = SolicitudEntity(0, equipoId, ambienteDestino, proposito, duracionHoras, estado)
}
