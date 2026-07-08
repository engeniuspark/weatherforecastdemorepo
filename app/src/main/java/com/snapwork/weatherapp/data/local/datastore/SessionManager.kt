package com.snapwork.weatherapp.data.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SessionManager(private val context: Context) {
    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_session")
        private val KEY_USER_EMAIL = stringPreferencesKey("user_email")
    }

    val userSessionFlow: Flow<String?> = context.dataStore.data
        .map { preferences ->
            preferences[KEY_USER_EMAIL]
        }

    suspend fun saveSession(email: String) {
        context.dataStore.edit { preferences ->
            preferences[KEY_USER_EMAIL] = email
        }
    }

    suspend fun clearSession() {
        context.dataStore.edit { preferences ->
            preferences.remove(KEY_USER_EMAIL)
        }
    }
}
