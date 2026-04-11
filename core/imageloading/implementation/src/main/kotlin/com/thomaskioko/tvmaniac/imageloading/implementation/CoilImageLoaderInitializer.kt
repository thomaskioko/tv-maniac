package com.thomaskioko.tvmaniac.imageloading.implementation

import coil.Coil
import com.thomaskioko.tvmaniac.core.base.di.Initializers
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.IntoSet
import dev.zacsweers.metro.Provides

@Inject
public class CoilImageLoaderInitializer(
    private val imageLoaderFactory: CoilImageLoaderFactory,
) : AppInitializer {
    override fun init() {
        Coil.setImageLoader(imageLoaderFactory.create())
    }
}
