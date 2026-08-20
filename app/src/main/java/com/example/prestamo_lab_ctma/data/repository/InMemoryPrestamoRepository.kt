package com.example.prestamo_lab_ctma.data.repository

import com.example.prestamo_lab_ctma.model.*

class InMemoryPrestamoRepository : PrestamoRepository {

    private var equipos = mutableListOf(
        Equipo(1, "Multímetro digital", CategoriaEquipo.ELECTRONICA, EstadoEquipo.DISPONIBLE),
        Equipo(2, "Kit Arduino", CategoriaEquipo.ELECTRONICA, EstadoEquipo.DISPONIBLE),
        Equipo(3, "Cámara digital", CategoriaEquipo.MULTIMEDIA, EstadoEquipo.DISPONIBLE),
        Equipo(4, "Tablet educativa", CategoriaEquipo.INFORMATICA, EstadoEquipo.RESERVADO),
        Equipo(5, "Taladro inalámbrico", CategoriaEquipo.HERRAMIENTAS, EstadoEquipo.PRESTADO),
        Equipo(6, "Fuente de alimentación", CategoriaEquipo.ELECTRONICA, EstadoEquipo.DISPONIBLE),
        Equipo(7, "Protoboard", CategoriaEquipo.ELECTRONICA, EstadoEquipo.DISPONIBLE),
        Equipo(8, "Cámara web", CategoriaEquipo.MULTIMEDIA, EstadoEquipo.DISPONIBLE)
    )

    private var solicitudes = mutableListOf<SolicitudPrestamo>()
    private var nextSolicitudId = 1

    override fun obtenerEquipos(): List<Equipo> = equipos

    override fun obtenerEquipo(id: Int): Equipo? = equipos.find { it.id == id }

    override fun obtenerSolicitudes(): List<SolicitudPrestamo> = solicitudes

    override fun obtenerSolicitud(id: Int): SolicitudPrestamo? = solicitudes.find { it.id == id }

    override fun crearSolicitud(solicitud: SolicitudPrestamo): Result<Unit> {
        val equipo = obtenerEquipo(solicitud.equipoId)
            ?: return Result.failure(Exception("Equipo no encontrado"))

        if (equipo.estado != EstadoEquipo.DISPONIBLE) {
            return Result.failure(Exception("El equipo no está disponible"))
        }

        // RN-06: Cambiar equipo a RESERVADO y solicitud a SOLICITADA
        val nuevaSolicitud = solicitud.copy(id = nextSolicitudId++, estado = EstadoSolicitud.SOLICITADA)
        solicitudes.add(nuevaSolicitud)

        actualizarEstadoEquipo(solicitud.equipoId, EstadoEquipo.RESERVADO)

        return Result.success(Unit)
    }

    override fun cancelarSolicitud(id: Int): Result<Unit> {
        val index = solicitudes.indexOfFirst { it.id == id }
        if (index == -1) return Result.failure(Exception("Solicitud no encontrada"))

        val solicitud = solicitudes[index]
        
        // RN-07: Solo una solicitud SOLICITADA puede cancelarse
        if (solicitud.estado != EstadoSolicitud.SOLICITADA) {
            return Result.failure(Exception("Solo se pueden cancelar solicitudes en estado SOLICITADA"))
        }

        // RN-15: Cambiarla a CANCELADA y actualizar disponibilidad del equipo
        solicitudes[index] = solicitud.copy(estado = EstadoSolicitud.CANCELADA)
        actualizarEstadoEquipo(solicitud.equipoId, EstadoEquipo.DISPONIBLE)

        return Result.success(Unit)
    }

    private fun actualizarEstadoEquipo(equipoId: Int, nuevoEstado: EstadoEquipo) {
        val index = equipos.indexOfFirst { it.id == equipoId }
        if (index != -1) {
            equipos[index] = equipos[index].copy(estado = nuevoEstado)
        }
    }
}
