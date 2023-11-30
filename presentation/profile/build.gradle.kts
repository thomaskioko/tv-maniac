import org.jetbrains.compose.compose

plugins {
    id("plugin.tvmaniac.multiplatform")
}


kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.core.traktAuth.api)
                implementation(projects.data.profile.api)
                implementation(projects.data.profilestats.api)

                api(compose("org.jetbrains.compose.runtime:runtime"))
                api(compose("org.jetbrains.compose.runtime:runtime-saveable"))

                implementation(libs.kotlinInject.runtime)
                implementation(libs.kotlinx.collections)
                implementation(libs.sqldelight.extensions)
                implementation(libs.voyager.core)
            }
        }

        commonTest {
            dependencies {
                implementation(kotlin("test"))
                implementation(projects.core.datastore.testing)
                implementation(projects.data.profile.testing)
                implementation(projects.core.traktAuth.testing)

                implementation(libs.bundles.unittest)
            }
        }
    }
}
