plugins {
    id("plugin.tvmaniac.multiplatform")
    alias(libs.plugins.ksp)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.core.util)
                implementation(projects.data.category.api)
                implementation(projects.data.showimages.api)
                implementation(projects.data.shows.api)

                api(libs.decompose.decompose)
                api(libs.kotlinx.collections)

                implementation(libs.kotlinInject.runtime)
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