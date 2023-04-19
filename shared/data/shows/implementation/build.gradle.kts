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
            implementation(projects.shared.core.traktApi.api)
            implementation(projects.shared.core.util)
            implementation(projects.shared.data.category.api)
            implementation(projects.shared.data.shows.api)

            api(libs.coroutines.core)

            implementation(libs.kermit)
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