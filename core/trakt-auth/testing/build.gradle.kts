plugins {
    id("tvmaniac.kmm.library")
}


kotlin {
    android()
    ios()

    sourceSets {
        sourceSets["commonMain"].dependencies {
            implementation(projects.core.traktAuth.api)
        }
    }
}

android {
    namespace = "com.thomaskioko.tvmaniac.traktauth.testing"
}