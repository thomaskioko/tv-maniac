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
 * Composes every [NavRouteBinding] contributed at [ActivityScope] into one polymorphic
 * [KSerializer] of [NavRoute].
 *
 * Decompose's per-tab `childStack(serializer = ...)` uses the result so back stacks survive
 * configuration change and process death. The constructor validates that at least one binding
 * is present and that no [NavRoute] subclass contributes more than once. Validation runs at
 * construction so misconfigured graphs fail before any navigation happens.
 *
 * @param bindings polymorphic serializer entries for each registered [NavRoute] subclass.
 */
@OptIn(ExperimentalStateKeeperApi::class, ExperimentalSerializationApi::class)
@SingleIn(ActivityScope::class)
@ContributesBinding(ActivityScope::class)
public class DefaultNavRouteSerializer(
    bindings: Set<NavRouteBinding<*>>,
) : NavRouteSerializer {
    init {
        require(bindings.isNotEmpty()) {
            "DefaultNavRouteSerializer requires at least one NavRouteBinding. " +
                "Contribute via @IntoSet on Set<NavRouteBinding<*>>."
        }
        val duplicates = bindings.map { it.kClass }
            .groupingBy { it }.eachCount()
            .filterValues { it > 1 }
            .keys
        require(duplicates.isEmpty()) {
            "Duplicate NavRouteBindings for: ${duplicates.map { it.simpleName }}. " +
                "Each NavRoute class may only contribute one binding."
        }
    }

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
