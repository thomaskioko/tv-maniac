package com.thomaskioko.tvmaniac.subscription.testing

import com.thomaskioko.tvmaniac.subscription.api.SubscriptionStatus
import com.thomaskioko.tvmaniac.subscription.api.SubscriptionStatusProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

public class FakeSubscriptionStatusProvider : SubscriptionStatusProvider {

    private val _status = MutableStateFlow(SubscriptionStatus.Free)

    public fun setStatus(status: SubscriptionStatus) {
        _status.value = status
    }

    override fun observeStatus(): Flow<SubscriptionStatus> = _status
}
