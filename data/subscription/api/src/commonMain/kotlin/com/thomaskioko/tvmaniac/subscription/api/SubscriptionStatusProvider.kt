package com.thomaskioko.tvmaniac.subscription.api

import kotlinx.coroutines.flow.Flow

public interface SubscriptionStatusProvider {
    public fun observeStatus(): Flow<SubscriptionStatus>
}
