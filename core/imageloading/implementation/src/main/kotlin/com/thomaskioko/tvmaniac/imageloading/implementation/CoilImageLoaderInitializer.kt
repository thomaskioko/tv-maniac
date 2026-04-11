package com.thomaskioko.tvmaniac.imageloading.implementation

import coil.Coil
import dev.zacsweers.metro.Inject

@Inject
public class CoilImageLoaderInitializer(
    private val imageLoaderFactory: CoilImageLoaderFactory,
) {
    public fun init() {
        Coil.setImageLoader(imageLoaderFactory.create())
    }
}
