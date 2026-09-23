package com.ctma.prestamolab.data.local.converter

/**
 * Room no puede guardar enums de Kotlin directamente en SQLite; los
 * TypeConverters no se usan aquí porque optamos por guardar el enum
 * ya como String (ver EquipoEntity/SolicitudEntity), así que este
 * archivo concentra las funciones de conversión enum <-> String que
 * usan los Mappers, evitando errores de escritura del nombre a mano
 * en cada lugar que lo necesite.
 */
import com.ctma.prestamolab.model.CategoriaEquipo
import com.ctma.prestamolab.model.EstadoEquipo
import com.ctma.prestamolab.model.EstadoSolicitud

fun CategoriaEquipo.aTexto(): String = this.name
fun String.aCategoriaEquipo(): CategoriaEquipo = CategoriaEquipo.valueOf(this)

fun EstadoEquipo.aTexto(): String = this.name
fun String.aEstadoEquipo(): EstadoEquipo = EstadoEquipo.valueOf(this)

fun EstadoSolicitud.aTexto(): String = this.name
fun String.aEstadoSolicitud(): EstadoSolicitud = EstadoSolicitud.valueOf(this)
