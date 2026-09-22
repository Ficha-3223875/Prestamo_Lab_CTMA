package com.example.prestamo_lab_ctma.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

class UserPreferencesRepository(private val context: Context) {
    private val CATEGORY_FILTER = stringPreferencesKey("category_filter")

    val categoryFilter: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[CATEGORY_FILTER]
    }

    suspend fun saveCategoryFilter(category: String?) {
        context.dataStore.edit { preferences ->
            if (category == null) {
                preferences.remove(CATEGORY_FILTER)
            } else {
                preferences[CATEGORY_FILTER] = category
            }
        }
    }
}
