plugins {
    id("plugin.tvmaniac.multiplatform")
}


kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.data.library.api)

                api(libs.decompose.decompose)
                api(libs.essenty.lifecycle)
                api(libs.kotlinx.collections)

                implementation(libs.kotlinInject.runtime)
            }
        }

        commonTest {
            dependencies {
                implementation(kotlin("test"))
                implementation(projects.data.library.testing)

                implementation(libs.bundles.unittest)
            }
        }
    }
}
