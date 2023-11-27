package com.thomaskioko.tvmaniac.inject

import android.app.Activity
import com.thomaskioko.tvmaniac.MainActivityViewModel
import com.thomaskioko.tvmaniac.common.navigation.VoyagerScreenModelComponent
import com.thomaskioko.tvmaniac.common.navigation.inject.VoyagerUiComponent
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthManager
import com.thomaskioko.tvmaniac.traktauth.implementation.TraktAuthManagerComponent
import com.thomaskioko.tvmaniac.util.scope.ActivityScope
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides

@ActivityScope
@Component
abstract class MainActivityComponent(
    @get:Provides val activity: Activity,
    @Component val applicationComponent: ApplicationComponent = ApplicationComponent.from(activity),
) : TraktAuthManagerComponent, VoyagerScreenModelComponent, VoyagerUiComponent {
    abstract val traktAuthManager: TraktAuthManager
    abstract val viewModel: () -> MainActivityViewModel

    val bind: VoyagerScreenModelComponent
        @Provides get() = this
}
