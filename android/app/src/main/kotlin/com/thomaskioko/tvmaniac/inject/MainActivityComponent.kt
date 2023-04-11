package com.thomaskioko.tvmaniac.inject

import android.app.Activity
import com.thomaskioko.tvmaniac.base.scope.ActivityScope
import com.thomaskioko.tvmaniac.core.networkutil.NetworkRepository
import com.thomaskioko.tvmaniac.datastore.api.DatastoreRepository
import com.thomaskioko.tvmaniac.traktauth.inject.TraktAuthManagerComponent
import dagger.hilt.android.components.ActivityComponent
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides

@ActivityScope
@Component
abstract class MainActivityComponent(
    @get:Provides val activity: Activity,
    @Component val applicationComponent: ApplicationComponent = ApplicationComponent.from(activity),
) : ActivityComponent,
    TraktAuthManagerComponent {
    abstract val datastoreRepository: DatastoreRepository
    abstract val networkRepository: NetworkRepository
}