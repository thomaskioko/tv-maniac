package com.thomaskioko.tvmaniac.imageloading.implementation

import coil.Coil
import com.thomaskioko.tvmaniac.core.base.AppInitializer
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding

@Inject
@ContributesBinding(AppScope::class, multibinding = true)
public class CoilImageLoaderInitializer(
    private val imageLoaderFactory: CoilImageLoaderFactory,
) : AppInitializer {
    override fun init() {
        Coil.setImageLoader(imageLoaderFactory.create())
    }
}
