package com.example.prestamo_lab_ctma.model

enum class CategoriaEquipo {
    ELECTRONICA,
    MULTIMEDIA,
    INFORMATICA,
    HERRAMIENTAS,
    OTRO
}

enum class EstadoEquipo {
    DISPONIBLE,
    RESERVADO,
    PRESTADO
}

enum class EstadoSolicitud {
    SOLICITADA,
    APROBADA,
    ENTREGADA,
    DEVUELTA,
    CANCELADA,
    RECHAZADA
}
