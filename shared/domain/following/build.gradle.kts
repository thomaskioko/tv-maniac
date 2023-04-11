plugins {
    id("tvmaniac.kmm.domain")
}


kotlin {
    android()
    ios()

    sourceSets {
        sourceSets["commonMain"].dependencies {
            implementation(project(":shared:data:shows:api"))
        }

        sourceSets["commonTest"].dependencies {
            implementation(project(":shared:data:shows:testing"))
        }

    }
}

android {
    namespace = "com.thomaskioko.tvmaniac.domain.following"
}
