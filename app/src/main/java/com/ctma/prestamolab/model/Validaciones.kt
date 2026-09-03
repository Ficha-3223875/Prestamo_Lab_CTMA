package com.ctma.prestamolab.model

/**
 * Reglas de negocio expresadas como funciones puras (sección 10.4 de la guía),
 * sin depender de ningún composable. Esto permite probarlas con pruebas
 * unitarias (JUnit) sin levantar la interfaz.
 */

/** RN-02: el ambiente/destino es obligatorio. */
fun ambienteValido(texto: String): Boolean =
    texto.trim().isNotEmpty()

/** RN-03: propósito entre 10 y 180 caracteres (equivalencia + límites 9/10/180/181). */
fun propositoValido(texto: String): Boolean =
    texto.trim().length in 10..180

/** RN-04: duración entre 1 y 8 horas (límites 0/1/8/9). */
fun duracionValida(horas: Int): Boolean =
    horas in 1..8

/** RN-01: solo puede solicitarse un equipo DISPONIBLE. */
fun equipoDisponibleParaSolicitud(equipo: Equipo?): Boolean =
    equipo != null && equipo.estado == EstadoEquipo.DISPONIBLE

/** RN-07: en este MVP solo una solicitud SOLICITADA puede cancelarse. */
fun puedeCancelarse(solicitud: SolicitudPrestamo?): Boolean =
    solicitud != null && solicitud.estado == EstadoSolicitud.SOLICITADA

/** RN-10: solo una solicitud SOLICITADA puede aprobarse o rechazarse. */
fun puedeAprobarse(solicitud: SolicitudPrestamo?): Boolean =
    solicitud != null && solicitud.estado == EstadoSolicitud.SOLICITADA

fun puedeRechazarse(solicitud: SolicitudPrestamo?): Boolean =
    solicitud != null && solicitud.estado == EstadoSolicitud.SOLICITADA

/** RN-11: solo una solicitud APROBADA puede marcarse como ENTREGADA. */
fun puedeEntregarse(solicitud: SolicitudPrestamo?): Boolean =
    solicitud != null && solicitud.estado == EstadoSolicitud.APROBADA

/** RN-12: solo una solicitud ENTREGADA puede marcarse como DEVUELTA. */
fun puedeDevolverse(solicitud: SolicitudPrestamo?): Boolean =
    solicitud != null && solicitud.estado == EstadoSolicitud.ENTREGADA

/**
 * Valida el formulario completo de una solicitud nueva.
 * Devuelve null si es válido, o un mensaje específico por campo si no lo es
 * (RN-02/03/04 + RN-01), evitando el mensaje genérico "debe funcionar correctamente".
 */
fun validarFormularioSolicitud(
    equipo: Equipo?,
    ambienteDestino: String,
    proposito: String,
    duracionHoras: Int
): String? = when {
    !equipoDisponibleParaSolicitud(equipo) ->
        "Este equipo ya no está disponible para solicitud."
    !ambienteValido(ambienteDestino) ->
        "Indica el ambiente o destino del préstamo."
    !propositoValido(proposito) ->
        "El propósito debe tener entre 10 y 180 caracteres."
    !duracionValida(duracionHoras) ->
        "La duración debe estar entre 1 y 8 horas."
    else -> null
}
