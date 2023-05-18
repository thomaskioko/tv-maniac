plugins {
    id("tvmaniac.kmm.library")
    alias(libs.plugins.ksp)
}

kotlin {
    android()
    ios()

    sourceSets {

        sourceSets["commonMain"].dependencies {

            api(libs.coroutines.core)

            implementation(projects.core.database)
            implementation(projects.core.traktApi.api)
            implementation(projects.core.util)
            implementation(projects.data.category.api)
            implementation(projects.data.profile.api)
            implementation(projects.data.watchlist.api)

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
    namespace = "com.thomaskioko.tvmaniac.watchlist.implementation"
}