package com.ctma.prestamolab.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.ctma.prestamolab.model.CategoriaEquipo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Contrato de las preferencias del usuario. Separado de la
 * implementación real (Semana 7) para poder sustituirlo por una
 * versión de prueba en JVM pura, sin depender de un Context de
 * Android real — necesario para probar PrestamoViewModel con
 * kotlinx-coroutines-test (ver PrestamoViewModelTest.kt).
 */
interface PreferenciasFiltro {
    val categoriaFiltro: Flow<CategoriaEquipo?>
    suspend fun guardarCategoriaFiltro(categoria: CategoriaEquipo?)
}

private val Context.dataStore by preferencesDataStore(name = "prestamolab_preferencias")

class FiltrosPreferences(private val context: Context) : PreferenciasFiltro {

    private val CATEGORIA_FILTRO_KEY = stringPreferencesKey("categoria_filtro")

    override val categoriaFiltro: Flow<CategoriaEquipo?> = context.dataStore.data.map { prefs ->
        prefs[CATEGORIA_FILTRO_KEY]?.let { valor ->
            runCatching { CategoriaEquipo.valueOf(valor) }.getOrNull()
        }
    }

    override suspend fun guardarCategoriaFiltro(categoria: CategoriaEquipo?) {
        context.dataStore.edit { prefs ->
            if (categoria == null) {
                prefs.remove(CATEGORIA_FILTRO_KEY)
            } else {
                prefs[CATEGORIA_FILTRO_KEY] = categoria.name
            }
        }
    }
}
