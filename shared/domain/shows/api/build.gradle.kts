plugins {
    id("tvmaniac.kmm.api")
}


kotlin {
    android()
    ios()

    sourceSets {

        sourceSets["commonMain"].dependencies {
            implementation(project(":shared:domain:trakt:api"))
            implementation(project(":shared:domain:tmdb:api"))

            api(libs.flowredux)
        }

        sourceSets["commonTest"].dependencies {

            implementation(project(":shared:domain:trakt:testing"))
            implementation(project(":shared:domain:tmdb:testing"))
            implementation(kotlin("test"))

            implementation(libs.testing.turbine)
            implementation(libs.testing.coroutines.test)
            implementation(libs.testing.kotest.assertions)
        }

    }
}

android {
    namespace = "com.thomaskioko.tvmaniac.shared.domain.show_common.api"
}
