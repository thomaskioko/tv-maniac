plugins {
    id("tvmaniac.kmm.library")
    alias(libs.plugins.serialization)
}

kotlin {
    android()
    ios()

    sourceSets {
        sourceSets["commonMain"].dependencies {
            api(project(":shared:networkutil"))
            api(project(":shared:data:database"))

            implementation(libs.ktor.serialization)
        }

    }
}

android {
    namespace = "com.thomaskioko.tvmaniac.tmdb.api"
}
