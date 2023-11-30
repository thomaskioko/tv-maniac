import org.jetbrains.compose.compose

plugins {
    id("plugin.tvmaniac.multiplatform")
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.core.util)
                implementation(projects.data.category.api)
                implementation(projects.data.showimages.api)
                implementation(projects.data.shows.api)

                api(compose("org.jetbrains.compose.runtime:runtime"))
                api(compose("org.jetbrains.compose.runtime:runtime-saveable"))

                implementation(libs.kotlinInject.runtime)
                implementation(libs.kotlinx.collections)
                implementation(libs.voyager.core)
            }
        }

        commonTest {
            dependencies {
                implementation(kotlin("test"))

                implementation(projects.data.showimages.testing)
                implementation(projects.data.shows.testing)

                implementation(libs.bundles.unittest)
            }
        }
    }
}