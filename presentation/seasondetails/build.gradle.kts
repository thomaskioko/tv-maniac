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

                api(libs.kotlinx.collections)

                implementation(libs.flowredux)
                implementation(libs.kotlinInject.runtime)
            }
        }

        commonTest {
            dependencies {
                implementation(kotlin("test"))
                implementation(projects.data.episodeimages.testing)
                implementation(projects.data.seasondetails.testing)

                implementation(libs.coroutines.test)
                implementation(libs.kotest.assertions)
                implementation(libs.turbine)
            }
        }
    }
}

dependencies {
    add("kspIosX64", libs.kotlinInject.compiler)
    add("kspIosArm64", libs.kotlinInject.compiler)
}
