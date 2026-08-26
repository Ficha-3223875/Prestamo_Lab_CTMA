package com.ctma.prestamolab.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ctma.prestamolab.data.repository.PrestamoRepository

/**
 * Fábrica simple para inyectar el Repository en el ViewModel sin librerías
 * externas de inyección de dependencias (fuera del alcance del MVP).
 */
class PrestamoViewModelFactory(
    private val repository: PrestamoRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PrestamoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PrestamoViewModel(repository) as T
        }
        throw IllegalArgumentException("ViewModel desconocido: ${modelClass.name}")
    }
}
