package com.thomaskioko.tvmaniac.imageloading.implementation

import coil.Coil
import com.thomaskioko.tvmaniac.core.base.AppInitializer
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.binding

@Inject
@ContributesBinding(AppScope::class, binding = binding<AppInitializer>())
class CoilImageLoaderInitializer(
    private val imageLoaderFactory: CoilImageLoaderFactory,
) : AppInitializer {
    override fun init() {
        Coil.setImageLoader(imageLoaderFactory.create())
    }
}
