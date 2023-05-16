plugins {
    id("tvmaniac.kmm.library")
    alias(libs.plugins.ksp)
}

kotlin {
    android()
    ios()

    sourceSets {
        sourceSets["commonMain"].dependencies {
            implementation(projects.core.database)
            implementation(projects.core.datastore.api)
            implementation(projects.core.tmdbApi.api)
            implementation(projects.core.traktApi.api)
            implementation(projects.core.util)
            implementation(projects.data.episodes.api)
            implementation(projects.data.seasons.api)

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
    add("kspAndroid", libs.kotlinInject.compiler)
    add("kspIosX64", libs.kotlinInject.compiler)
    add("kspIosArm64", libs.kotlinInject.compiler)
}

android {
    namespace = "com.thomaskioko.tvmaniac.seasons.implementation"
}