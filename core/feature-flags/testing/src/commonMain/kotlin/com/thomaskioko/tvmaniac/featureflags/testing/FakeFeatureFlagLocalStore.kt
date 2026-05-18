package com.thomaskioko.tvmaniac.featureflags.testing

import com.thomaskioko.tvmaniac.featureflags.FeatureFlagLocalStore
import com.thomaskioko.tvmaniac.featureflags.model.FeatureFlag
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlin.reflect.KClass

public class FakeFeatureFlagLocalStore : FeatureFlagLocalStore {

    private val valuesFlow: MutableStateFlow<Map<FeatureFlag, Any>> = MutableStateFlow(emptyMap())

    override fun <T : Any> observe(flag: FeatureFlag, type: KClass<T>): Flow<T?> =
        valuesFlow
            .map { values ->
                val value = values[flag]
                @Suppress("UNCHECKED_CAST")
                if (value != null && type.isInstance(value)) value as T else null
            }
            .distinctUntilChanged()

    override fun observeAll(): Flow<Map<FeatureFlag, Any>> = valuesFlow

    override suspend fun <T : Any> set(flag: FeatureFlag, value: T) {
        valuesFlow.update { it + (flag to value) }
    }

    override suspend fun clear(flag: FeatureFlag) {
        valuesFlow.update { it - flag }
    }

    override suspend fun clearAll() {
        valuesFlow.update { emptyMap() }
    }
}
