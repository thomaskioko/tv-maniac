plugins {
    id("plugin.tvmaniac.multiplatform")
    alias(libs.plugins.ksp)
}


kotlin {

    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.data.episodeimages.api)
                implementation(projects.data.episodes.api)
                implementation(projects.data.seasondetails.api)

                implementation(libs.kotlinx.collections)
                implementation(libs.voyager.core)
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

dependencies {
    add("kspIosX64", libs.kotlinInject.compiler)
    add("kspIosArm64", libs.kotlinInject.compiler)
}
