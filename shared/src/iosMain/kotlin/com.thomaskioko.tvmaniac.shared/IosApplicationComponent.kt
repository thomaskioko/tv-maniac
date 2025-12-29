package com.thomaskioko.tvmaniac.shared

import com.thomaskioko.tvmaniac.core.base.AppInitializers
import com.thomaskioko.tvmaniac.core.logger.Logger
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthManager
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthRepository
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.MergeComponent
import software.amazon.lastmile.kotlin.inject.anvil.MergeComponent.CreateComponent
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn
import kotlin.reflect.KClass

@MergeComponent(AppScope::class)
@SingleIn(AppScope::class)
public abstract class IosApplicationComponent : IosViewPresenterComponent.Factory {
    public abstract val initializers: AppInitializers
    public abstract val componentFactory: IosViewPresenterComponent.Factory
    public abstract val traktAuthRepository: TraktAuthRepository
    public abstract val traktAuthManager: TraktAuthManager
    public abstract val logger: Logger

    public companion object {
        public fun create(): IosApplicationComponent = IosApplicationComponent::class.createComponent()
    }
}

/**
 * The `actual fun` will be generated for each iOS specific target. See [MergeComponent] for more
 * details.
 */
@CreateComponent
public expect fun KClass<IosApplicationComponent>.createComponent(): IosApplicationComponent
