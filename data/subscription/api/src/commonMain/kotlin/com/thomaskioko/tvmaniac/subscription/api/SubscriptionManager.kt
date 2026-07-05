package com.thomaskioko.tvmaniac.subscription.api

import kotlinx.coroutines.flow.Flow

public interface SubscriptionManager {
    public fun observeAccess(feature: SubscriptionFeature): Flow<Boolean>

    public suspend fun hasAccess(feature: SubscriptionFeature): Boolean

    public fun observeSubscriptionStatus(): Flow<SubscriptionStatus>
}
