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
abstract class IosApplicationComponent : IosViewPresenterComponent.Factory {
    abstract val initializers: AppInitializers
    abstract val componentFactory: IosViewPresenterComponent.Factory
    abstract val traktAuthRepository: TraktAuthRepository
    abstract val traktAuthManager: TraktAuthManager
    abstract val logger: Logger

    companion object {
        fun create() = IosApplicationComponent::class.createComponent()
    }
}

/**
 * The `actual fun` will be generated for each iOS specific target. See [MergeComponent] for more
 * details.
 */
@CreateComponent
expect fun KClass<IosApplicationComponent>.createComponent(): IosApplicationComponent
