package com.thomaskioko.tvmaniac.shared

import com.arkivanov.decompose.ComponentContext
import com.thomaskioko.tvmaniac.navigation.RootNavigationPresenter
import com.thomaskioko.tvmaniac.traktauth.implementation.TraktAuthManagerComponent
import com.thomaskioko.tvmaniac.util.scope.ActivityScope
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides

@Component
@ActivityScope
abstract class IosViewPresenterComponent(
    @get:Provides val componentContext: ComponentContext,
    @Component val applicationComponent: ApplicationComponent,
) : TraktAuthManagerComponent {
    abstract val presenter: RootNavigationPresenter

    companion object
}
