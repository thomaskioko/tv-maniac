plugins {
    id("tvmaniac.kmm.library")
}


kotlin {
    android()
    ios()

    sourceSets {

        sourceSets["commonMain"].dependencies {
            api(projects.shared.core.database)
            api(libs.coroutines.core)

        }
    }
}

android {
    namespace = "com.thomaskioko.tvmaniac.data.category.api"
}
