package com.thomaskioko.tvmaniac.inject

import android.app.Activity
import com.thomaskioko.tvmaniac.MainActivityViewModel
import com.thomaskioko.tvmaniac.base.scope.ActivityScope
import com.thomaskioko.tvmaniac.traktauth.TraktAuthManager
import com.thomaskioko.tvmaniac.traktauth.inject.TraktAuthManagerComponent
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides

@ActivityScope
@Component
abstract class MainActivityComponent(
    @get:Provides val activity: Activity,
    @Component val applicationComponent: ApplicationComponent = ApplicationComponent.from(activity),
) : TraktAuthManagerComponent {
    abstract val traktAuthManager: TraktAuthManager
    abstract val viewModel: () -> MainActivityViewModel
}