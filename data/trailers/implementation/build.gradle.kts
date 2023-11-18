plugins {
    id("plugin.tvmaniac.multiplatform")
    alias(libs.plugins.ksp)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.core.tmdbApi.api)
                implementation(projects.core.util)
                implementation(projects.data.requestManager.api)
                implementation(projects.data.shows.api)
                implementation(projects.data.trailers.api)

                implementation(libs.kermit)
                implementation(libs.kotlinInject.runtime)
                implementation(libs.sqldelight.extensions)
                implementation(libs.kotlinx.atomicfu)
                implementation(libs.store5)
            }
        }
    }
}

dependencies {
    add("kspIosX64", libs.kotlinInject.compiler)
    add("kspIosArm64", libs.kotlinInject.compiler)
}