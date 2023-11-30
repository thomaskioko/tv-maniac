import org.jetbrains.compose.compose

plugins {
    id("plugin.tvmaniac.multiplatform")
}


kotlin {
    sourceSets {

        commonMain {
            dependencies {
                implementation(projects.data.trailers.api)

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
                implementation(projects.data.trailers.testing)

                implementation(libs.bundles.unittest)
            }
        }
    }
}