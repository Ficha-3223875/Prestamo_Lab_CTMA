package com.ctma.prestamolab.data.repository

import com.ctma.prestamolab.data.local.converter.aTexto
import com.ctma.prestamolab.data.local.dao.EquipoDao
import com.ctma.prestamolab.data.local.dao.SolicitudDao
import com.ctma.prestamolab.data.local.entity.EquipoEntity
import com.ctma.prestamolab.model.CategoriaEquipo
import com.ctma.prestamolab.model.Equipo
import com.ctma.prestamolab.model.EstadoEquipo
import com.ctma.prestamolab.model.EstadoSolicitud
import com.ctma.prestamolab.model.SolicitudPrestamo
import com.ctma.prestamolab.model.mapper.aDominio
import com.ctma.prestamolab.model.mapper.aEntidad
import com.ctma.prestamolab.model.puedeAprobarse
import com.ctma.prestamolab.model.puedeDevolverse
import com.ctma.prestamolab.model.puedeEntregarse
import com.ctma.prestamolab.model.puedeRechazarse
import com.ctma.prestamolab.model.validarFormularioSolicitud

/**
 * Implementación real con Room (Semana 6, actividad 9: "Refactorizar
 * el Repository para incorporar una fuente local Room"). Sustituye a
 * InMemoryPrestamoRepository como fuente de datos de la app; Room es
 * ahora la fuente única de verdad (sección 4.4: "Room actúa como
 * fuente local canónica").
 *
 * InMemoryPrestamoRepository NO se borra: se conserva para las
 * pruebas unitarias (JVM, sin depender de Android/SQLite) y como
 * referencia de la línea base heredada.
 */
class RoomPrestamoRepository(
    private val equipoDao: EquipoDao,
    private val solicitudDao: SolicitudDao
) : PrestamoRepository {

    /** Carga el catálogo semilla la primera vez que la base de datos está vacía. */
    suspend fun sembrarCatalogoSiEstaVacio() {
        if (equipoDao.contar() == 0) {
            val semilla = listOf(
                EquipoEntity(nombre = "Multímetro digital", categoria = CategoriaEquipo.ELECTRONICA.aTexto(), estado = EstadoEquipo.DISPONIBLE.aTexto()),
                EquipoEntity(nombre = "Kit de electrónica básica", categoria = CategoriaEquipo.ELECTRONICA.aTexto(), estado = EstadoEquipo.DISPONIBLE.aTexto()),
                EquipoEntity(nombre = "Tableta Android (demo)", categoria = CategoriaEquipo.INFORMATICA.aTexto(), estado = EstadoEquipo.DISPONIBLE.aTexto()),
                EquipoEntity(nombre = "Cámara réflex", categoria = CategoriaEquipo.AUDIOVISUAL.aTexto(), estado = EstadoEquipo.DISPONIBLE.aTexto()),
                EquipoEntity(nombre = "Kit de destornilladores", categoria = CategoriaEquipo.HERRAMIENTA_MANUAL.aTexto(), estado = EstadoEquipo.PRESTADO.aTexto()),
                EquipoEntity(nombre = "Adaptador HDMI-VGA", categoria = CategoriaEquipo.OTRO.aTexto(), estado = EstadoEquipo.DISPONIBLE.aTexto())
            )
            equipoDao.insertarTodos(semilla)
        }
    }

    override suspend fun obtenerEquipos(): List<Equipo> {
        // La siembra se dispara aquí mismo, no desde afuera: así no
        // depende de que MainActivity recuerde llamarla en el orden
        // correcto (corregido antes de tener ese bug en producción).
        sembrarCatalogoSiEstaVacio()
        return equipoDao.obtenerTodos().map { it.aDominio() }
    }

    override suspend fun obtenerEquipo(id: Int): Equipo? =
        equipoDao.obtenerPorId(id)?.aDominio()

    override suspend fun obtenerSolicitudes(): List<SolicitudPrestamo> =
        solicitudDao.obtenerTodas().map { it.aDominio() }

    override suspend fun obtenerSolicitud(id: Int): SolicitudPrestamo? =
        solicitudDao.obtenerPorId(id)?.aDominio()

    override suspend fun crearSolicitud(
        equipoId: Int,
        ambienteDestino: String,
        proposito: String,
        duracionHoras: Int
    ): Result<SolicitudPrestamo> {
        val equipoEntity = equipoDao.obtenerPorId(equipoId)
        val equipo = equipoEntity?.aDominio()
        val mensajeError = validarFormularioSolicitud(equipo, ambienteDestino, proposito, duracionHoras)
        if (mensajeError != null) {
            return Result.failure(IllegalArgumentException(mensajeError))
        }

        // RN-06: la solicitud reserva el equipo de inmediato.
        equipoDao.actualizar(equipoEntity!!.copy(estado = EstadoEquipo.RESERVADO.aTexto()))

        val nuevaEntity = SolicitudPrestamo(
            id = 0,
            equipoId = equipoId,
            ambienteDestino = ambienteDestino.trim(),
            proposito = proposito.trim(),
            duracionHoras = duracionHoras,
            estado = EstadoSolicitud.SOLICITADA
        ).aEntidad()

        val nuevoId = solicitudDao.insertar(nuevaEntity)
        val creada = solicitudDao.obtenerPorId(nuevoId.toInt())!!.aDominio()
        return Result.success(creada)
    }

    override suspend fun cancelarSolicitud(id: Int): Result<Unit> =
        transicionar(
            id = id,
            condicion = { it != null && it.estado == EstadoSolicitud.SOLICITADA },
            nuevoEstadoSolicitud = EstadoSolicitud.CANCELADA,
            nuevoEstadoEquipo = EstadoEquipo.DISPONIBLE,
            mensajeError = "Solo una solicitud SOLICITADA puede cancelarse."
        )

    override suspend fun aprobarSolicitud(id: Int): Result<Unit> =
        transicionar(
            id = id,
            condicion = ::puedeAprobarse,
            nuevoEstadoSolicitud = EstadoSolicitud.APROBADA,
            nuevoEstadoEquipo = null,
            mensajeError = "Solo una solicitud SOLICITADA puede aprobarse."
        )

    override suspend fun rechazarSolicitud(id: Int): Result<Unit> =
        transicionar(
            id = id,
            condicion = ::puedeRechazarse,
            nuevoEstadoSolicitud = EstadoSolicitud.RECHAZADA,
            nuevoEstadoEquipo = EstadoEquipo.DISPONIBLE,
            mensajeError = "Solo una solicitud SOLICITADA puede rechazarse."
        )

    override suspend fun entregarSolicitud(id: Int): Result<Unit> =
        transicionar(
            id = id,
            condicion = ::puedeEntregarse,
            nuevoEstadoSolicitud = EstadoSolicitud.ENTREGADA,
            nuevoEstadoEquipo = EstadoEquipo.PRESTADO,
            mensajeError = "Solo una solicitud APROBADA puede marcarse como entregada."
        )

    override suspend fun devolverSolicitud(id: Int): Result<Unit> =
        transicionar(
            id = id,
            condicion = ::puedeDevolverse,
            nuevoEstadoSolicitud = EstadoSolicitud.DEVUELTA,
            nuevoEstadoEquipo = EstadoEquipo.DISPONIBLE,
            mensajeError = "Solo una solicitud ENTREGADA puede marcarse como devuelta."
        )

    private suspend fun transicionar(
        id: Int,
        condicion: (SolicitudPrestamo?) -> Boolean,
        nuevoEstadoSolicitud: EstadoSolicitud,
        nuevoEstadoEquipo: EstadoEquipo?,
        mensajeError: String
    ): Result<Unit> {
        val entidad = solicitudDao.obtenerPorId(id)
        val solicitud = entidad?.aDominio()
        if (!condicion(solicitud)) {
            return Result.failure(IllegalStateException(mensajeError))
        }
        solicitudDao.actualizar(entidad!!.copy(estado = nuevoEstadoSolicitud.aTexto()))

        if (nuevoEstadoEquipo != null) {
            val equipoEntity = equipoDao.obtenerPorId(entidad.equipoId)
            if (equipoEntity != null) {
                equipoDao.actualizar(equipoEntity.copy(estado = nuevoEstadoEquipo.aTexto()))
            }
        }
        return Result.success(Unit)
    }
}
