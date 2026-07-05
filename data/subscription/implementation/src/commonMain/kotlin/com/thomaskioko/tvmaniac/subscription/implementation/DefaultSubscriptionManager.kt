package com.thomaskioko.tvmaniac.subscription.implementation

import com.thomaskioko.tvmaniac.appconfig.DebugConfig
import com.thomaskioko.tvmaniac.datastore.api.DatastoreRepository
import com.thomaskioko.tvmaniac.featureflags.FeatureFlag
import com.thomaskioko.tvmaniac.featureflags.flags.EnablePaywallFlagQualifier
import com.thomaskioko.tvmaniac.subscription.api.AccountType
import com.thomaskioko.tvmaniac.subscription.api.SubscriptionFeature
import com.thomaskioko.tvmaniac.subscription.api.SubscriptionManager
import com.thomaskioko.tvmaniac.subscription.api.SubscriptionStatus
import com.thomaskioko.tvmaniac.subscription.api.SubscriptionStatusProvider
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class DefaultSubscriptionManager(
    @EnablePaywallFlagQualifier
    private val enablePaywallFlag: FeatureFlag<Boolean>,
    private val subscriptionStatusProvider: SubscriptionStatusProvider,
    private val datastoreRepository: DatastoreRepository,
    private val debugConfig: DebugConfig,
) : SubscriptionManager {

    override fun observeAccess(feature: SubscriptionFeature): Flow<Boolean> =
        combine(
            enablePaywallFlag.observe(),
            observeSubscriptionStatus(),
        ) { paywallEnabled, status ->
            !paywallEnabled || status == SubscriptionStatus.Premium
        }

    override suspend fun hasAccess(feature: SubscriptionFeature): Boolean = observeAccess(feature).first()

    override fun observeSubscriptionStatus(): Flow<SubscriptionStatus> =
        combine(
            subscriptionStatusProvider.observeStatus(),
            datastoreRepository.observeAccountType(),
        ) { status, overrideName ->
            val override = AccountType.fromName(overrideName)
            override.toDebugStatus()?.takeIf { debugConfig.isDebug } ?: status
        }

    private fun AccountType.toDebugStatus(): SubscriptionStatus? = when (this) {
        AccountType.Premium -> SubscriptionStatus.Premium
        AccountType.Free -> SubscriptionStatus.Free
        AccountType.None -> null
    }
}
