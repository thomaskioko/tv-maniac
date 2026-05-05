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
public class DefaultNavRootSerializer(
    bindings: Set<NavRootBinding<*>>,
) : NavRootSerializer {
    init {
        require(bindings.isNotEmpty()) {
            "DefaultNavRootSerializer requires at least one NavRootBinding. " +
                "Contribute via @IntoSet on Set<NavRootBinding<*>>."
        }
        val duplicates = bindings.map { it.kClass }
            .groupingBy { it }.eachCount()
            .filterValues { it > 1 }
            .keys
        require(duplicates.isEmpty()) {
            "Duplicate NavRootBindings for: ${duplicates.map { it.simpleName }}. " +
                "Each NavRoot class may only contribute one binding."
        }
    }

    private val module: SerializersModule = SerializersModule {
        polymorphic(NavRoot::class) {
            bindings.forEach { binding ->
                @Suppress("UNCHECKED_CAST")
                subclass(
                    binding.kClass as KClass<NavRoot>,
                    binding.serializer as KSerializer<NavRoot>,
                )
            }
        }
    }

    override val serializer: KSerializer<NavRoot> = polymorphicSerializer(
        baseClass = NavRoot::class,
        module = module,
    )
}
