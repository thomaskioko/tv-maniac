plugins {
    id("plugin.tvmaniac.multiplatform")
    alias(libs.plugins.ksp)
}

kotlin {

    sourceSets {

        commonMain {
            dependencies {
                implementation(projects.core.util)
                implementation(projects.data.seasons.api)
                implementation(projects.data.similar.api)
                implementation(projects.data.trailers.api)
                implementation(projects.data.shows.api)
                implementation(projects.data.watchlist.api)

                implementation(libs.flowredux)
                implementation(libs.kotlinInject.runtime)

            }
        }

        commonTest {
            dependencies {
                implementation(kotlin("test"))
                implementation(projects.data.shows.testing)
                implementation(projects.data.seasons.testing)
                implementation(projects.data.similar.testing)
                implementation(projects.data.trailers.testing)
                implementation(projects.data.watchlist.testing)

                implementation(libs.coroutines.test)
                implementation(libs.kotest.assertions)
                implementation(libs.turbine)
            }
        }
    }
}

dependencies {
    add("kspIosX64", libs.kotlinInject.compiler)
    add("kspIosArm64", libs.kotlinInject.compiler)
}