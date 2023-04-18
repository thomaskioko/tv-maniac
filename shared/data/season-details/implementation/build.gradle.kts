plugins {
    id("tvmaniac.kmm.library")
    alias(libs.plugins.ksp)
}

kotlin {
    android()
    ios()

    sourceSets {
        sourceSets["commonMain"].dependencies {
            implementation(projects.shared.core.database)
            implementation(projects.shared.core.datastore.api)
            implementation(projects.shared.core.tmdbApi.api)
            implementation(projects.shared.core.traktApi.api)
            implementation(projects.shared.core.util)
            implementation(projects.shared.data.episodes.api)
            implementation(projects.shared.data.seasonDetails.api)

            implementation(libs.kermit)
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
    namespace = "com.thomaskioko.tvmaniac.seasondetails.implementation"
}