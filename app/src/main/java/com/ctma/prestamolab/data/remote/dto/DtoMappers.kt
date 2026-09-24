package com.ctma.prestamolab.data.remote.dto

import com.ctma.prestamolab.data.local.entity.EquipoEntity
import com.ctma.prestamolab.data.local.entity.SolicitudEntity

/**
 * Mapeo DTO <-> Entity (Semana 8, actividad 24). El mapeo dominio <->
 * Entity ya existía desde la Semana 6 (EntityMappers.kt); este archivo
 * agrega el tercer lado del triángulo: DTO <-> Entity, para que un
 * EquipoDto recién bajado del servidor se pueda guardar directo en
 * Room con el mismo formato que ya usa el resto de la app.
 */

fun EquipoDto.aEntidad(): EquipoEntity = EquipoEntity(
    id = id,
    nombre = nombre,
    categoria = categoria,
    estado = estado
)

fun SolicitudDto.aEntidad(): SolicitudEntity = SolicitudEntity(
    id = id,
    equipoId = equipoId,
    ambienteDestino = ambienteDestino,
    proposito = proposito,
    duracionHoras = duracionHoras,
    estado = estado
)
