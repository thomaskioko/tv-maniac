plugins {
    id("tvmaniac.kmm.data")
}


kotlin {
    android()
    ios()

    sourceSets {

        sourceSets["commonMain"].dependencies {
            api(project(":shared:core:networkutil"))
            implementation(project(":shared:data:shows:api"))
        }

    }
}

android {
    namespace = "com.thomaskioko.tvmaniac.similar.api"
}
