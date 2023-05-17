plugins {
    id("tvmaniac.kmm.library")
    alias(libs.plugins.ksp)
}

kotlin {
    android()
    ios()

    sourceSets {

        sourceSets["androidMain"].dependencies {
            implementation(libs.ktor.okhttp)
        }

        sourceSets["commonMain"].dependencies {
            implementation(projects.core.util)
            implementation(projects.data.showimages.api)
            implementation(projects.core.tmdbApi.api)

            implementation(libs.kotlinInject.runtime)
            implementation(libs.sqldelight.extensions)
        }

    }
}

android {
    namespace = "com.thomaskioko.tvmaniac.showimages.implementation"
}

dependencies {
    add("kspIosX64", libs.kotlinInject.compiler)
    add("kspIosArm64", libs.kotlinInject.compiler)
}