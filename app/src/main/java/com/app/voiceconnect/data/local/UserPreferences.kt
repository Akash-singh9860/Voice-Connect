package com.app.voiceconnect.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_settings")

/**
 * Manages user-specific preferences using Jetpack DataStore.
 * This class provides a way to store and retrieve the user's identity persistently.
 */
@Singleton
class UserPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val userIdKey = stringPreferencesKey("user_identity")

    /**
     * A [Flow] that emits the current user identity stored in preferences.
     * Emits null if no identity has been saved.
     */
    val userIdentity: Flow<String?> = context.dataStore.data
        .map { preferences ->
            preferences[userIdKey]
        }

    /**
     * Saves the provided [identity] to the persistent data store.
     *
     * @param identity The user identity string to be stored.
     */
    suspend fun saveIdentity(identity: String) {
        context.dataStore.edit { preferences ->
            preferences[userIdKey] = identity
        }
    }

    /**
     * Clears all stored preferences, effectively resetting the user settings.
     */
    suspend fun clear() {
        context.dataStore.edit { it.clear() }
    }
}
