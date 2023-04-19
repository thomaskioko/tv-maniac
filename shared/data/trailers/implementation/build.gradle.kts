plugins {
    id("tvmaniac.kmm.library")
    alias(libs.plugins.ksp)
}


kotlin {
    android()
    ios()

    sourceSets {
        sourceSets["commonMain"].dependencies {
            implementation(projects.shared.core.tmdbApi.api)
            implementation(projects.shared.core.util)
            implementation(projects.shared.data.shows.api)
            implementation(projects.shared.data.trailers.api)

            implementation(libs.kermit)
            implementation(libs.kotlinInject.runtime)
            implementation(libs.sqldelight.extensions)
        }
    }
}

dependencies {
    add("kspAndroid", libs.kotlinInject.compiler)
    add("kspIosX64", libs.kotlinInject.compiler)
    add("kspIosArm64", libs.kotlinInject.compiler)
}

android {
    namespace = "com.thomaskioko.tvmaniac.data.trailers.implementation"
}