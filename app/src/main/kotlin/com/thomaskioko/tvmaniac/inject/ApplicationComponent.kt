package com.thomaskioko.tvmaniac.inject

import android.app.Application
import android.content.Context
import com.thomaskioko.tvmaniac.TvManicApplication
import com.thomaskioko.tvmaniac.initializers.AppInitializers
import com.thomaskioko.tvmaniac.shared.SharedComponent
import com.thomaskioko.tvmaniac.traktauth.implementation.TraktAuthComponent
import com.thomaskioko.tvmaniac.util.scope.ApplicationScope
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides

@Component
@ApplicationScope
abstract class ApplicationComponent(
    @get:Provides val application: Application,
) : SharedComponent(), TraktAuthComponent {

    abstract val initializers: AppInitializers

    companion object {
        fun from(context: Context): ApplicationComponent {
            return (context.applicationContext as TvManicApplication).component
        }
    }
}
