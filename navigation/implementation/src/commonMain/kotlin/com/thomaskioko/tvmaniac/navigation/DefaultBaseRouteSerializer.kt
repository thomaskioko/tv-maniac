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

/**
 * Composes every [NavRouteBinding] and [NavRootBinding] contributed at [ActivityScope] into one
 * polymorphic [KSerializer] of [BaseRoute].
 *
 * Decompose's `childStack(serializer = ...)` uses the resulting serializer when each tab's back
 * stack mixes a [NavRoot] at the bottom with [NavRoute] entries on top. The constructor validates
 * three invariants: at least one binding is present, every contributed [NavRoot] also has a
 * matching [NavRootBinding], and no route or root class contributes more than one binding.
 * Validation runs once at construction so misconfigured graphs fail at startup rather than at the
 * first navigation event.
 *
 * @param routeBindings polymorphic serializer entries for each [NavRoute] subclass.
 * @param rootBindings polymorphic serializer entries for each [NavRoot] subclass.
 * @param navRoots tab anchors registered through the navigation multibinding set; used to verify
 *   each has a matching [NavRootBinding].
 */
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
