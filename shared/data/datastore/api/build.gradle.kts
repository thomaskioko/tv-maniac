plugins {
    id("tvmaniac.kmm.api")
}


kotlin {
    android()
    ios()

    sourceSets {
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
