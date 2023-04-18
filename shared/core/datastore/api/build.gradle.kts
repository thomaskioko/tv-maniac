plugins {
    id("tvmaniac.kmm.library")
}


kotlin {
    android()
    ios()

    sourceSets {

        sourceSets["commonMain"].dependencies {
            api(libs.coroutines.core)
        }

        sourceSets["commonTest"].dependencies {
            implementation(kotlin("test"))

            implementation(libs.coroutines.test)
            implementation(libs.kotest.assertions)
            implementation(libs.turbine)
        }

    }
}

android {
    namespace = "com.thomaskioko.tvmaniac.shared.domain.datastore.api"
}
