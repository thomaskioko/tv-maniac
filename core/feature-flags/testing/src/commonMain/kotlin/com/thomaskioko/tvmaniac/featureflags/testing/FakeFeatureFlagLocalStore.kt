package com.thomaskioko.tvmaniac.featureflags.testing

import com.thomaskioko.tvmaniac.featureflags.FeatureFlagLocalStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlin.reflect.KClass

public class FakeFeatureFlagLocalStore : FeatureFlagLocalStore {

    private val valuesFlow: MutableStateFlow<Map<String, Any>> = MutableStateFlow(emptyMap())

    override fun <T : Any> observe(key: String, type: KClass<T>): Flow<T?> =
        valuesFlow
            .map { values ->
                val value = values[key]
                @Suppress("UNCHECKED_CAST")
                if (value != null && type.isInstance(value)) value as T else null
            }
            .distinctUntilChanged()

    override fun observeAll(): Flow<Map<String, Any>> = valuesFlow

    override suspend fun <T : Any> set(key: String, value: T) {
        valuesFlow.update { it + (key to value) }
    }

    override suspend fun clear(key: String) {
        valuesFlow.update { it - key }
    }

    override suspend fun clearAll() {
        valuesFlow.update { emptyMap() }
    }
}
