package com.thomaskioko.tvmaniac.featureflags

import com.thomaskioko.tvmaniac.featureflags.model.FeatureFlag
import kotlinx.coroutines.flow.Flow
import kotlin.reflect.KClass

public interface FeatureFlagLocalStore {
    public fun <T : Any> observe(flag: FeatureFlag, type: KClass<T>): Flow<T?>

    public fun observeAll(): Flow<Map<FeatureFlag, Any>>

    public suspend fun <T : Any> set(flag: FeatureFlag, value: T)

    public suspend fun clear(flag: FeatureFlag)

    public suspend fun clearAll()
}

public inline fun <reified T : Any> FeatureFlagLocalStore.observe(flag: FeatureFlag): Flow<T?> =
    observe(flag, T::class)
