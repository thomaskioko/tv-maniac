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
import com.thomaskioko.tvmaniac.featureflags.model.FeatureFlag
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

    override fun <T : Any> observe(flag: FeatureFlag, type: KClass<T>): Flow<T?> {
        val key = keyFor(flag, type)
        return dataStore.data
            .map { prefs -> prefs[key] }
            .distinctUntilChanged()
    }

    override fun observeAll(): Flow<Map<FeatureFlag, Any>> {
        val flagsByKey = FeatureFlag.entries.associateBy { it.key }
        return dataStore.data
            .map { prefs ->
                prefs.asMap()
                    .mapNotNull { (prefKey, value) ->
                        flagsByKey[prefKey.name]?.let { flag -> flag to value }
                    }
                    .toMap()
            }
            .distinctUntilChanged()
    }

    override suspend fun <T : Any> set(flag: FeatureFlag, value: T) {
        dataStore.edit { prefs ->
            when (value) {
                is Boolean -> prefs[booleanPreferencesKey(flag.key)] = value
                is Int -> prefs[intPreferencesKey(flag.key)] = value
                is Long -> prefs[longPreferencesKey(flag.key)] = value
                is Float -> prefs[floatPreferencesKey(flag.key)] = value
                is Double -> prefs[doublePreferencesKey(flag.key)] = value
                is String -> prefs[stringPreferencesKey(flag.key)] = value
                else -> error("Unsupported feature flag value type: ${value::class.simpleName}")
            }
        }
    }

    override suspend fun clear(flag: FeatureFlag) {
        dataStore.edit { prefs ->
            prefs.asMap().keys
                .filter { it.name == flag.key }
                .forEach { prefs.remove(it) }
        }
    }

    override suspend fun clearAll() {
        dataStore.edit { prefs -> prefs.clear() }
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T : Any> keyFor(flag: FeatureFlag, type: KClass<T>): Preferences.Key<T> = when (type) {
        Boolean::class -> booleanPreferencesKey(flag.key) as Preferences.Key<T>
        Int::class -> intPreferencesKey(flag.key) as Preferences.Key<T>
        Long::class -> longPreferencesKey(flag.key) as Preferences.Key<T>
        Float::class -> floatPreferencesKey(flag.key) as Preferences.Key<T>
        Double::class -> doublePreferencesKey(flag.key) as Preferences.Key<T>
        String::class -> stringPreferencesKey(flag.key) as Preferences.Key<T>
        else -> error("Unsupported feature flag value type: ${type.simpleName}")
    }
}
