package com.ctma.prestamolab.model

/**
 * Categorías simuladas del catálogo de equipos de laboratorio.
 * Se limita a un conjunto pequeño porque el incremento es un prototipo educativo.
 */
enum class CategoriaEquipo {
    ELECTRONICA,
    INFORMATICA,
    HERRAMIENTA_MANUAL,
    AUDIOVISUAL,
    OTRO
}

/**
 * Disponibilidad real de un equipo dentro del prototipo.
 * DISPONIBLE -> puede solicitarse (RN-01).
 * RESERVADO -> ya tiene una solicitud activa (RN-06).
 * PRESTADO -> fue entregado y aún no ha sido devuelto.
 */
enum class EstadoEquipo {
    DISPONIBLE,
    RESERVADO,
    PRESTADO
}

/**
 * Ciclo de vida de una solicitud de préstamo (sección 4.6 de la guía).
 * En este MVP la única transición manual habilitada desde la UI es
 * SOLICITADA -> CANCELADA (RN-07). El resto queda modelado para que
 * el equipo pueda habilitarlo como reto o con datos controlados.
 */
enum class EstadoSolicitud {
    SOLICITADA,
    APROBADA,
    ENTREGADA,
    DEVUELTA,
    CANCELADA,
    RECHAZADA
}
