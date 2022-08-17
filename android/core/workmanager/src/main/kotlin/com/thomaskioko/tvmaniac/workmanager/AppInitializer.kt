package com.thomaskioko.tvmaniac.workmanager

/**
 * This would ideally be in a 'core' module but since we are only using it in the work manager,
 * we can leave it here for now.
 */
fun interface AppInitializer {
    fun init()
}
