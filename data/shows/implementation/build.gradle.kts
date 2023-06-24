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
            implementation(projects.core.traktApi.api)
            implementation(projects.core.util)
            implementation(projects.data.shows.api)
            implementation(projects.data.requestManager.api)

            api(libs.coroutines.core)

            implementation(libs.kotlinInject.runtime)
            implementation(libs.sqldelight.extensions)
        }

    }
}

dependencies {
    add("kspIosX64", libs.kotlinInject.compiler)
    add("kspIosArm64", libs.kotlinInject.compiler)
}

android {
    namespace = "com.thomaskioko.tvmaniac.shows.implementation"
}