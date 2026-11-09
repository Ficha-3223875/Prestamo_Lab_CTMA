package com.example.prestamo_lab_ctma.data.remote

import com.example.prestamo_lab_ctma.data.remote.dto.EquipoDto
import retrofit2.http.GET

interface ApiService {
    @GET("equipos")
    suspend fun obtenerEquiposRemotos(): List<EquipoDto>
}
