package com.ctma.prestamolab.model.mapper

import com.ctma.prestamolab.data.local.converter.aCategoriaEquipo
import com.ctma.prestamolab.data.local.converter.aEstadoEquipo
import com.ctma.prestamolab.data.local.converter.aEstadoSolicitud
import com.ctma.prestamolab.data.local.converter.aTexto
import com.ctma.prestamolab.data.local.entity.EquipoEntity
import com.ctma.prestamolab.data.local.entity.EvidenciaEntity
import com.ctma.prestamolab.data.local.entity.SolicitudEntity
import com.ctma.prestamolab.model.EstadoEvidencia
import com.ctma.prestamolab.model.Equipo
import com.ctma.prestamolab.model.EvidenciaFoto
import com.ctma.prestamolab.model.SolicitudPrestamo

fun EquipoEntity.aDominio(): Equipo = Equipo(
    id = id,
    nombre = nombre,
    categoria = categoria.aCategoriaEquipo(),
    estado = estado.aEstadoEquipo()
)

fun Equipo.aEntidad(): EquipoEntity = EquipoEntity(
    id = id,
    nombre = nombre,
    categoria = categoria.aTexto(),
    estado = estado.aTexto()
)

fun SolicitudEntity.aDominio(): SolicitudPrestamo = SolicitudPrestamo(
    id = id,
    equipoId = equipoId,
    ambienteDestino = ambienteDestino,
    proposito = proposito,
    duracionHoras = duracionHoras,
    estado = estado.aEstadoSolicitud()
)

fun SolicitudPrestamo.aEntidad(): SolicitudEntity = SolicitudEntity(
    id = id,
    equipoId = equipoId,
    ambienteDestino = ambienteDestino,
    proposito = proposito,
    duracionHoras = duracionHoras,
    estado = estado.aTexto()
)

// --- Semana 9: evidencia fotográfica ---

fun EvidenciaEntity.aDominio(): EvidenciaFoto = EvidenciaFoto(
    id = id,
    solicitudId = solicitudId,
    uri = uri,
    fechaCapturaMillis = fechaCapturaMillis,
    luxAlCapturar = luxAlCapturar,
    estado = EstadoEvidencia.valueOf(estado)
)

fun EvidenciaFoto.aEntidad(): EvidenciaEntity = EvidenciaEntity(
    id = id,
    solicitudId = solicitudId,
    uri = uri,
    fechaCapturaMillis = fechaCapturaMillis,
    luxAlCapturar = luxAlCapturar,
    estado = estado.name
)
