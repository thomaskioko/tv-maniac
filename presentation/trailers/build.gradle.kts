plugins {
    id("plugin.tvmaniac.multiplatform")
}


kotlin {
    sourceSets {

        commonMain {
            dependencies {
                implementation(projects.data.trailers.api)

                api(libs.decompose.decompose)
                api(libs.kotlinx.collections)

                implementation(libs.kotlinInject.runtime)
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