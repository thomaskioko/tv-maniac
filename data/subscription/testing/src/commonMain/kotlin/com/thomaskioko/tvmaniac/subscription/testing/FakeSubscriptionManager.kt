package com.thomaskioko.tvmaniac.subscription.testing

import com.thomaskioko.tvmaniac.subscription.api.SubscriptionFeature
import com.thomaskioko.tvmaniac.subscription.api.SubscriptionManager
import com.thomaskioko.tvmaniac.subscription.api.SubscriptionStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

public class FakeSubscriptionManager : SubscriptionManager {

    private val _status = MutableStateFlow(SubscriptionStatus.Free)
    private val _access = MutableStateFlow(SubscriptionFeature.entries.associateWith { true })

    public fun setStatus(status: SubscriptionStatus) {
        _status.value = status
    }

    public fun setAccess(feature: SubscriptionFeature, hasAccess: Boolean) {
        _access.value = _access.value + (feature to hasAccess)
    }

    override fun observeAccess(feature: SubscriptionFeature): Flow<Boolean> = _access.map { it.getValue(feature) }

    override suspend fun hasAccess(feature: SubscriptionFeature): Boolean = _access.value.getValue(feature)

    override fun observeSubscriptionStatus(): Flow<SubscriptionStatus> = _status
}
