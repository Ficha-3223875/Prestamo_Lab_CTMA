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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

/**
 * Implementación real con Room. Desde la Semana 7, observarEquipos()
 * y observarSolicitudes() son Flow: cualquier INSERT/UPDATE que haga
 * cualquier método de esta clase dispara automáticamente una nueva
 * emisión, sin necesidad de "avisar" manualmente. Esto es lo que
 * permite eliminar por completo el patrón "hacer la acción, luego
 * recargar" que tenía el ViewModel hasta la Semana 6.
 */
class RoomPrestamoRepository(
    private val equipoDao: EquipoDao,
    private val solicitudDao: SolicitudDao
) : PrestamoRepository {

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

    // onStart{} corre la siembra ANTES de la primera emisión del
    // Flow, sin bloquear la app ni requerir una llamada aparte desde
    // MainActivity (mismo problema de orden que resolvimos en
    // Semana 6, pero ahora expresado con el operador correcto).
    override fun observarEquipos(): Flow<List<Equipo>> =
        equipoDao.observarTodos()
            .map { entidades -> entidades.map { it.aDominio() } }
            .onStart { sembrarCatalogoSiEstaVacio() }

    override fun observarSolicitudes(): Flow<List<SolicitudPrestamo>> =
        solicitudDao.observarTodas().map { entidades -> entidades.map { it.aDominio() } }

    override suspend fun obtenerEquipo(id: Int): Equipo? =
        equipoDao.obtenerPorId(id)?.aDominio()

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
        transicionar(id, { it != null && it.estado == EstadoSolicitud.SOLICITADA }, EstadoSolicitud.CANCELADA, EstadoEquipo.DISPONIBLE, "Solo una solicitud SOLICITADA puede cancelarse.")

    override suspend fun aprobarSolicitud(id: Int): Result<Unit> =
        transicionar(id, ::puedeAprobarse, EstadoSolicitud.APROBADA, null, "Solo una solicitud SOLICITADA puede aprobarse.")

    override suspend fun rechazarSolicitud(id: Int): Result<Unit> =
        transicionar(id, ::puedeRechazarse, EstadoSolicitud.RECHAZADA, EstadoEquipo.DISPONIBLE, "Solo una solicitud SOLICITADA puede rechazarse.")

    override suspend fun entregarSolicitud(id: Int): Result<Unit> =
        transicionar(id, ::puedeEntregarse, EstadoSolicitud.ENTREGADA, EstadoEquipo.PRESTADO, "Solo una solicitud APROBADA puede marcarse como entregada.")

    override suspend fun devolverSolicitud(id: Int): Result<Unit> =
        transicionar(id, ::puedeDevolverse, EstadoSolicitud.DEVUELTA, EstadoEquipo.DISPONIBLE, "Solo una solicitud ENTREGADA puede marcarse como devuelta.")

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
