plugins {
    id("tvmaniac.kmm.domain")
}


kotlin {
    android()
    ios()

    sourceSets {

        sourceSets["commonMain"].dependencies {
            implementation(project(":shared:data:trailers:api"))
        }

        sourceSets["commonTest"].dependencies {
            implementation(project(":shared:data:trailers:testing"))
        }

    }
}

android {
    namespace = "com.thomaskioko.tvmaniac.data.trailers"
}
