package com.thomaskioko.tvmaniac.domain.widget

import com.thomaskioko.tvmaniac.core.base.interactor.SubjectInteractor
import com.thomaskioko.tvmaniac.datastore.api.AppTheme
import com.thomaskioko.tvmaniac.datastore.api.DatastoreRepository
import com.thomaskioko.tvmaniac.subscription.api.SubscriptionFeature
import com.thomaskioko.tvmaniac.subscription.api.SubscriptionManager
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

@Inject
public class ObserveWidgetThemeInteractor(
    private val datastoreRepository: DatastoreRepository,
    private val subscriptionManager: SubscriptionManager,
) : SubjectInteractor<Unit, AppTheme>() {

    override fun createObservable(params: Unit): Flow<AppTheme> = combine(
        datastoreRepository.observeWidgetTheme(),
        datastoreRepository.observeTheme(),
        subscriptionManager.observeAccess(SubscriptionFeature.WidgetTheming),
    ) { widgetTheme, appTheme, hasAccess ->
        if (hasAccess) widgetTheme ?: appTheme else appTheme
    }
}
