package com.thomaskioko.tvmaniac.subscription.implementation

import com.thomaskioko.tvmaniac.appconfig.DebugConfig
import com.thomaskioko.tvmaniac.core.base.IoCoroutineScope
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class DefaultSubscriptionManager(
    @EnablePaywallFlagQualifier
    enablePaywallFlag: FeatureFlag<Boolean>,
    subscriptionStatusProvider: SubscriptionStatusProvider,
    datastoreRepository: DatastoreRepository,
    private val debugConfig: DebugConfig,
    @IoCoroutineScope scope: CoroutineScope,
) : SubscriptionManager {

    private val subscriptionStatus: StateFlow<SubscriptionStatus?> =
        combine(
            subscriptionStatusProvider.observeStatus(),
            datastoreRepository.observeAccountType(),
        ) { status, overrideName ->
            val override = AccountType.fromName(overrideName)
            override.toDebugStatus()?.takeIf { debugConfig.isDebug } ?: status
        }.stateIn(scope, SharingStarted.Eagerly, null)

    private val featureAccess: StateFlow<Boolean?> =
        combine(
            enablePaywallFlag.observe(),
            subscriptionStatus.filterNotNull(),
        ) { paywallEnabled, status ->
            !paywallEnabled || status == SubscriptionStatus.Premium
        }.stateIn(scope, SharingStarted.Eagerly, null)

    override fun observeAccess(feature: SubscriptionFeature): Flow<Boolean> = featureAccess.filterNotNull()

    override suspend fun hasAccess(feature: SubscriptionFeature): Boolean = observeAccess(feature).first()

    override fun observeSubscriptionStatus(): Flow<SubscriptionStatus> = subscriptionStatus.filterNotNull()

    private fun AccountType.toDebugStatus(): SubscriptionStatus? = when (this) {
        AccountType.Premium -> SubscriptionStatus.Premium
        AccountType.Free -> SubscriptionStatus.Free
        AccountType.None -> null
    }
}
