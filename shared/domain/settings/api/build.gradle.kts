plugins {
    id("tvmaniac.kmm.api")
}


kotlin {
    android()
    ios()

    sourceSets {

        sourceSets["commonMain"].dependencies {
            api(libs.flowredux)

        }

        sourceSets["commonTest"].dependencies {
            implementation(kotlin("test"))

            implementation(libs.testing.coroutines.test)
            implementation(libs.testing.kotest.assertions)
            implementation(libs.testing.turbine)
        }

    }
}

android {
    namespace = "com.thomaskioko.tvmaniac.shared.domain.settings.api"
}
