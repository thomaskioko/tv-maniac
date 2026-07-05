package com.thomaskioko.tvmaniac.subscription.implementation

import com.thomaskioko.tvmaniac.subscription.api.SubscriptionStatus
import com.thomaskioko.tvmaniac.subscription.api.SubscriptionStatusProvider
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class DefaultSubscriptionStatusProvider : SubscriptionStatusProvider {
    override fun observeStatus(): Flow<SubscriptionStatus> = flowOf(SubscriptionStatus.Free)
}
