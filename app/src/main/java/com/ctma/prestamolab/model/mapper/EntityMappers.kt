package com.ctma.prestamolab.model.mapper

import com.ctma.prestamolab.data.local.converter.aCategoriaEquipo
import com.ctma.prestamolab.data.local.converter.aEstadoEquipo
import com.ctma.prestamolab.data.local.converter.aEstadoSolicitud
import com.ctma.prestamolab.data.local.converter.aTexto
import com.ctma.prestamolab.data.local.entity.EquipoEntity
import com.ctma.prestamolab.data.local.entity.SolicitudEntity
import com.ctma.prestamolab.model.Equipo
import com.ctma.prestamolab.model.SolicitudPrestamo

/**
 * Traducen entre el modelo de dominio (lo que usa la UI y el
 * ViewModel) y las entidades de Room (lo que se guarda en SQLite).
 * Mantener esta separación es lo que permite que Validaciones.kt y
 * el resto del dominio sigan sin saber nada de Room (sección 4.4 de
 * la guía: "la UI no debe conocer directamente Retrofit ni manipular
 * la base de datos"; lo mismo aplica al dominio respecto a Room).
 */

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
