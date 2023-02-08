plugins {
    id("tvmaniac.kmm.api")
}



kotlin {
    android()
    ios()

    sourceSets {

        sourceSets["commonMain"].dependencies {
            api(libs.flowredux)

            implementation(project(":shared:domain:season-details:api"))
            implementation(project(":shared:domain:similar:api"))
            implementation(project(":shared:domain:trailers:api"))
            implementation(project(":shared:domain:trakt:api"))
        }

        sourceSets["commonTest"].dependencies {
            implementation(project(":shared:domain:trakt:testing"))
            implementation(project(":shared:domain:similar:testing"))
            implementation(project(":shared:domain:season-details:testing"))
            implementation(project(":shared:domain:trailers:testing"))
            implementation(kotlin("test"))

            implementation(libs.testing.turbine)
            implementation(libs.testing.coroutines.test)
            implementation(libs.testing.kotest.assertions)
        }
    }
}

android {
    namespace = "com.thomaskioko.tvmaniac.shared.domain.details.api"
}
