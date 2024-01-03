plugins {
    id("plugin.tvmaniac.multiplatform")
    alias(libs.plugins.ksp)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.core.database)
                implementation(projects.core.tmdbApi.api)
                implementation(projects.core.util)
                implementation(projects.data.upcomingshows.api)
                implementation(projects.data.requestManager.api)

                api(libs.coroutines.core)

                implementation(libs.kotlinInject.runtime)
                implementation(libs.kotlinx.atomicfu)
                implementation(libs.sqldelight.extensions)
                implementation(libs.sqldelight.paging)
                implementation(libs.store5)
            }
        }
    }
}