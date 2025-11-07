package com.aktarjabed.nextgenrecorder.data.repository

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "settings")

class SettingsRepositoryImpl(private val context: Context) : SettingsRepository {
override suspend fun setAnalyticsEnabled(enabled: Boolean) {
context.dataStore.edit { preferences ->
preferences[ANALYTICS_ENABLED] = enabled
}
}

override fun getAnalyticsEnabled(): Flow<Boolean> {
return context.dataStore.data.map { preferences ->
preferences[ANALYTICS_ENABLED] ?: false
}
}
}

interface SettingsRepository {
suspend fun setAnalyticsEnabled(enabled: Boolean)
fun getAnalyticsEnabled(): Flow<Boolean>
}

private val ANALYTICS_ENABLED = booleanPreferencesKey("analytics_enabled")
