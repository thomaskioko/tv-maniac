plugins {
    id("plugin.tvmaniac.multiplatform")
}


kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.data.episodeimages.api)
                implementation(projects.data.episodes.api)
                implementation(projects.data.seasondetails.api)

                api(libs.decompose.decompose)
                api(libs.kotlinx.collections)

                implementation(libs.kotlinInject.runtime)
            }
        }

        commonTest {
            dependencies {
                implementation(kotlin("test"))
                implementation(projects.data.episodeimages.testing)
                implementation(projects.data.seasondetails.testing)

                implementation(libs.bundles.unittest)
            }
        }
    }
}