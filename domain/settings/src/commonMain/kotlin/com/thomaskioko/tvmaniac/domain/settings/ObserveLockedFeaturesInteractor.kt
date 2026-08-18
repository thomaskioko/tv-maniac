package com.thomaskioko.tvmaniac.domain.settings

import com.thomaskioko.tvmaniac.core.base.interactor.SubjectInteractor
import com.thomaskioko.tvmaniac.subscription.api.SubscriptionFeature
import com.thomaskioko.tvmaniac.subscription.api.SubscriptionManager
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

@Inject
public class ObserveLockedFeaturesInteractor(
    private val subscriptionManager: SubscriptionManager,
) : SubjectInteractor<Unit, LockedFeatures>() {

    override fun createObservable(params: Unit): Flow<LockedFeatures> = combine(
        subscriptionManager.observeAccess(SubscriptionFeature.CustomThemes),
        subscriptionManager.observeAccess(SubscriptionFeature.EpisodeNotifications),
        subscriptionManager.observeAccess(SubscriptionFeature.QuickRate),
        subscriptionManager.observeAccess(SubscriptionFeature.CloudBackup),
    ) { customThemes, episodeNotifications, quickRate, backup ->
        LockedFeatures(
            backupLocked = !backup,
            customThemesLocked = !customThemes,
            posterStyleLocked = !customThemes,
            episodeNotificationsLocked = !episodeNotifications,
            quickRateLocked = !quickRate,
        )
    }
}
