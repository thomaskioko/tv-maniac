plugins {
    id("tvmaniac.kmm.api")
}


kotlin {
    android()
    ios()

    sourceSets {

        sourceSets["commonMain"].dependencies {
            implementation(project(":shared:data:shows:api"))
        }

    }
}

android {
    namespace = "com.thomaskioko.tvmaniac.similar.api"
}
