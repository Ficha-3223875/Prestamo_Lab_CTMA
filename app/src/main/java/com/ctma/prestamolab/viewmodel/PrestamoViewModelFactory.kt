package com.ctma.prestamolab.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ctma.prestamolab.data.preferences.PreferenciasFiltro
import com.ctma.prestamolab.data.repository.PrestamoRepository

class PrestamoViewModelFactory(
    private val repository: PrestamoRepository,
    private val preferencias: PreferenciasFiltro
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PrestamoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PrestamoViewModel(repository, preferencias) as T
        }
        throw IllegalArgumentException("ViewModel desconocido: ${modelClass.name}")
    }
}
