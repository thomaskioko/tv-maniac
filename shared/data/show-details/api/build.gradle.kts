plugins {
    id("tvmaniac.kmm.api")
}



kotlin {
    android()
    ios()

    sourceSets {

        sourceSets["commonMain"].dependencies {
            api(libs.flowredux)

            implementation(project(":shared:data:season-details:api"))
            implementation(project(":shared:data:similar:api"))
            implementation(project(":shared:data:trailers:api"))
            implementation(project(":shared:data:trakt:api"))
        }

        sourceSets["commonTest"].dependencies {
            implementation(kotlin("test"))
            implementation(project(":shared:data:trakt:testing"))
            implementation(project(":shared:data:similar:testing"))
            implementation(project(":shared:data:season-details:testing"))
            implementation(project(":shared:data:trailers:testing"))

            implementation(libs.testing.turbine)
            implementation(libs.testing.coroutines.test)
            implementation(libs.testing.kotest.assertions)
        }
    }
}

android {
    namespace = "com.thomaskioko.tvmaniac.shared.domain.details.api"
}
