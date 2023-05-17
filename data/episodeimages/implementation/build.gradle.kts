plugins {
    id("tvmaniac.kmm.library")
    alias(libs.plugins.ksp)
}

kotlin {
    android()
    ios()

    sourceSets {

        sourceSets["commonMain"].dependencies {
            implementation(projects.core.tmdbApi.api)
            implementation(projects.data.episodeimages.api)

            implementation(libs.kotlinInject.runtime)
            implementation(libs.sqldelight.extensions)
        }

        sourceSets["commonTest"].dependencies {
            implementation(kotlin("test"))
            implementation(libs.turbine)
            implementation(libs.kotest.assertions)
        }

    }
}

dependencies {
    add("kspIosX64", libs.kotlinInject.compiler)
    add("kspIosArm64", libs.kotlinInject.compiler)
}

android {
    namespace = "com.thomaskioko.tvmaniac.episodeimages.implementation"
}
