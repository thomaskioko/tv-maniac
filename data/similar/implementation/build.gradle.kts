plugins {
    id("plugin.tvmaniac.multiplatform")
    alias(libs.plugins.ksp)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.core.tmdbApi.api)
                implementation(projects.core.traktApi.api)
                implementation(projects.core.util)
                implementation(projects.data.requestManager.api)
                implementation(projects.data.similar.api)
                implementation(projects.data.shows.api)

                implementation(libs.kotlinInject.runtime)
                implementation(libs.sqldelight.extensions)
            }
        }

        commonTest {
            dependencies {
                implementation(kotlin("test"))
                implementation(libs.turbine)
                implementation(libs.kotest.assertions)
                implementation(libs.coroutines.test)
            }
        }
    }
}

dependencies {
    add("kspIosX64", libs.kotlinInject.compiler)
    add("kspIosArm64", libs.kotlinInject.compiler)
}
