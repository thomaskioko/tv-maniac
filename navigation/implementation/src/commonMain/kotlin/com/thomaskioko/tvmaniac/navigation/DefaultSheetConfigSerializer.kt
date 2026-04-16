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
public class DefaultSheetConfigSerializer(
    bindings: Set<SheetConfigBinding<*>>,
) : SheetConfigSerializer {
    private val module: SerializersModule = SerializersModule {
        polymorphic(SheetConfig::class) {
            bindings.forEach { binding ->
                @Suppress("UNCHECKED_CAST")
                subclass(
                    binding.kClass as KClass<SheetConfig>,
                    binding.serializer as KSerializer<SheetConfig>,
                )
            }
        }
    }

    override val serializer: KSerializer<SheetConfig> = polymorphicSerializer(
        baseClass = SheetConfig::class,
        module = module,
    )
}
