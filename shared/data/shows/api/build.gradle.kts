plugins {
    id("tvmaniac.kmm.api")
}


kotlin {
    android()
    ios()

    sourceSets {

        sourceSets["commonMain"].dependencies {
            implementation(project(":shared:data:trakt:api"))
            implementation(project(":shared:data:tmdb:api"))

            api(libs.flowredux)
        }

        sourceSets["commonTest"].dependencies {

            implementation(project(":shared:data:trakt:testing"))
            implementation(project(":shared:data:tmdb:testing"))
            implementation(kotlin("test"))

            implementation(libs.testing.turbine)
            implementation(libs.testing.coroutines.test)
            implementation(libs.testing.kotest.assertions)
        }

    }
}

android {
    namespace = "com.thomaskioko.tvmaniac.shared.data.show_common.api"
}
