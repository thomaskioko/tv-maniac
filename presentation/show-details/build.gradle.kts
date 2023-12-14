plugins {
    id("plugin.tvmaniac.multiplatform")
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.core.util)
                implementation(projects.data.library.api)
                implementation(projects.data.seasons.api)
                implementation(projects.data.showdetails.api)
                implementation(projects.data.shows.api)
                implementation(projects.data.similar.api)
                implementation(projects.data.trailers.api)

                api(libs.decompose.decompose)
                api(libs.essenty.lifecycle)
                api(libs.kotlinx.collections)

                implementation(libs.kotlinInject.runtime)

            }
        }

        commonTest {
            dependencies {
                implementation(kotlin("test"))
                implementation(projects.data.seasons.testing)
                implementation(projects.data.similar.testing)
                implementation(projects.data.trailers.testing)
                implementation(projects.data.library.testing)

                implementation(libs.bundles.unittest)
            }
        }
    }
}
