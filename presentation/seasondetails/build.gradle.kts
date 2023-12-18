plugins {
    id("plugin.tvmaniac.multiplatform")
    alias(libs.plugins.serialization)
}


kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.core.util)
                implementation(projects.data.episodes.api)
                implementation(projects.data.recommendedshows.api)
                implementation(projects.data.seasondetails.api)
                implementation(projects.data.cast.api)

                api(libs.decompose.decompose)
                api(libs.essenty.lifecycle)
                api(libs.kotlinx.collections)

                implementation(libs.kotlinInject.runtime)
            }
        }

        commonTest {
            dependencies {
                implementation(kotlin("test"))
                implementation(projects.data.seasondetails.testing)

                implementation(libs.bundles.unittest)
            }
        }
    }
}