package com.thomaskioko.tvmaniac.featureflags.implementation

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.thomaskioko.tvmaniac.core.base.FeatureFlagLocalsDataStore
import com.thomaskioko.tvmaniac.featureflags.FeatureFlagLocalStore
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlin.reflect.KClass

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class DefaultFeatureFlagLocalStore(
    @FeatureFlagLocalsDataStore private val dataStore: DataStore<Preferences>,
) : FeatureFlagLocalStore {

    override fun <T : Any> observe(key: String, type: KClass<T>): Flow<T?> {
        val prefsKey = preferencesKeyFor(key, type)
        return dataStore.data
            .map { prefs -> prefs[prefsKey] }
            .distinctUntilChanged()
    }

    override fun observeAll(): Flow<Map<String, Any>> =
        dataStore.data
            .map { prefs ->
                prefs.asMap()
                    .mapKeys { (prefKey, _) -> prefKey.name }
            }
            .distinctUntilChanged()

    override suspend fun <T : Any> set(key: String, value: T) {
        dataStore.edit { prefs ->
            when (value) {
                is Boolean -> prefs[booleanPreferencesKey(key)] = value
                is Int -> prefs[intPreferencesKey(key)] = value
                is Long -> prefs[longPreferencesKey(key)] = value
                is Float -> prefs[floatPreferencesKey(key)] = value
                is Double -> prefs[doublePreferencesKey(key)] = value
                is String -> prefs[stringPreferencesKey(key)] = value
                else -> error("Unsupported feature flag value type: ${value::class.simpleName}")
            }
        }
    }

    override suspend fun clear(key: String) {
        dataStore.edit { prefs ->
            prefs.asMap().keys
                .filter { it.name == key }
                .forEach { prefs.remove(it) }
        }
    }

    override suspend fun clearAll() {
        dataStore.edit { prefs -> prefs.clear() }
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T : Any> preferencesKeyFor(key: String, type: KClass<T>): Preferences.Key<T> = when (type) {
        Boolean::class -> booleanPreferencesKey(key) as Preferences.Key<T>
        Int::class -> intPreferencesKey(key) as Preferences.Key<T>
        Long::class -> longPreferencesKey(key) as Preferences.Key<T>
        Float::class -> floatPreferencesKey(key) as Preferences.Key<T>
        Double::class -> doublePreferencesKey(key) as Preferences.Key<T>
        String::class -> stringPreferencesKey(key) as Preferences.Key<T>
        else -> error("Unsupported feature flag value type: ${type.simpleName}")
    }
}
