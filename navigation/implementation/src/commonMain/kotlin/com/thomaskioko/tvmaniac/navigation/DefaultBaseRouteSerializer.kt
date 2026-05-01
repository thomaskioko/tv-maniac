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
public class DefaultBaseRouteSerializer(
    routeBindings: Set<NavRouteBinding<*>>,
    rootBindings: Set<NavRootBinding<*>>,
) : BaseRouteSerializer {
    private val module: SerializersModule = SerializersModule {
        polymorphic(BaseRoute::class) {
            routeBindings.forEach { binding ->
                @Suppress("UNCHECKED_CAST")
                subclass(
                    binding.kClass as KClass<BaseRoute>,
                    binding.serializer as KSerializer<BaseRoute>,
                )
            }
            rootBindings.forEach { binding ->
                @Suppress("UNCHECKED_CAST")
                subclass(
                    binding.kClass as KClass<BaseRoute>,
                    binding.serializer as KSerializer<BaseRoute>,
                )
            }
        }
    }

    override val serializer: KSerializer<BaseRoute> = polymorphicSerializer(
        baseClass = BaseRoute::class,
        module = module,
    )
}
