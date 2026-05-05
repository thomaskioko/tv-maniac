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
    navRoots: Set<NavRoot>,
) : BaseRouteSerializer {
    init {
        require(routeBindings.isNotEmpty() || rootBindings.isNotEmpty()) {
            "BaseRoute serializer requires at least one NavRouteBinding or NavRootBinding. " +
                "Contribute via @IntoSet on Set<NavRouteBinding<*>> or Set<NavRootBinding<*>>."
        }

        val rootBindingClasses = rootBindings.mapTo(mutableSetOf()) { it.kClass }
        val missingRootBindings = navRoots.mapTo(mutableSetOf()) { it::class } - rootBindingClasses
        require(missingRootBindings.isEmpty()) {
            "Missing NavRootBinding for: ${missingRootBindings.map { it.simpleName }}. " +
                "Each NavRoot contributed to Set<NavRoot> must also contribute a NavRootBinding " +
                "via @IntoSet on Set<NavRootBinding<*>>."
        }

        val allBindingClasses = routeBindings.map { it.kClass } + rootBindings.map { it.kClass }
        val duplicates = allBindingClasses.groupingBy { it }.eachCount()
            .filterValues { it > 1 }
            .keys
        require(duplicates.isEmpty()) {
            "Duplicate BaseRoute bindings registered for: ${duplicates.map { it.simpleName }}. " +
                "Each route or root class may only contribute one binding."
        }
    }

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
