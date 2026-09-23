package com.ctma.prestamolab.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.ctma.prestamolab.model.CategoriaEquipo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * DataStore de preferencias del usuario (Semana 6, actividad 12:
 * "Configurar DataStore para filtros/preferencias del usuario").
 * Guarda qué categoría de equipo prefiere ver el usuario en el
 * catálogo, para que la próxima vez que abra la app recuerde su
 * último filtro. No es información sensible ni crítica: DataStore es
 * la herramienta correcta para esto (Room sería excesivo para una
 * sola preferencia simple).
 */
private val Context.dataStore by preferencesDataStore(name = "prestamolab_preferencias")

class FiltrosPreferences(private val context: Context) {

    private val CATEGORIA_FILTRO_KEY = stringPreferencesKey("categoria_filtro")

    /** null = sin filtro, mostrar todas las categorías. */
    val categoriaFiltro: Flow<CategoriaEquipo?> = context.dataStore.data.map { prefs ->
        prefs[CATEGORIA_FILTRO_KEY]?.let { valor ->
            runCatching { CategoriaEquipo.valueOf(valor) }.getOrNull()
        }
    }

    suspend fun guardarCategoriaFiltro(categoria: CategoriaEquipo?) {
        context.dataStore.edit { prefs ->
            if (categoria == null) {
                prefs.remove(CATEGORIA_FILTRO_KEY)
            } else {
                prefs[CATEGORIA_FILTRO_KEY] = categoria.name
            }
        }
    }
}
