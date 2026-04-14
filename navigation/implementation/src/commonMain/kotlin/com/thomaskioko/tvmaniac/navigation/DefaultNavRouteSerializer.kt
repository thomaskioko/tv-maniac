package com.thomaskioko.tvmaniac.navigation

import com.arkivanov.essenty.statekeeper.ExperimentalStateKeeperApi
import com.arkivanov.essenty.statekeeper.polymorphicSerializer
import com.thomaskioko.tvmaniac.core.base.ActivityScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlin.reflect.KClass

@OptIn(ExperimentalStateKeeperApi::class, ExperimentalSerializationApi::class)
@SingleIn(ActivityScope::class)
@ContributesBinding(ActivityScope::class)
public class DefaultNavRouteSerializer(
    bindings: Set<NavRouteBinding<*>>,
) : NavRouteSerializer {
    private val module: SerializersModule = SerializersModule {
        polymorphic(NavRoute::class) {
            bindings.forEach { binding ->
                @Suppress("UNCHECKED_CAST")
                subclass(
                    binding.kClass as KClass<NavRoute>,
                    binding.serializer as KSerializer<NavRoute>,
                )
            }
        }
    }

    override val serializer: KSerializer<NavRoute> = polymorphicSerializer(
        baseClass = NavRoute::class,
        module = module,
    )
}
