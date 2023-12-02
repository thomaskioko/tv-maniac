plugins {
    id("plugin.tvmaniac.multiplatform")
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {

                api(libs.decompose.decompose)
                implementation(libs.kotlinInject.runtime)
            }
        }

        commonTest {
            dependencies {
                implementation(kotlin("test"))

                implementation(libs.bundles.unittest)
            }
        }
    }
}