plugins {
    id("tvmaniac.kmm.domain")
}


kotlin {
    android()
    ios()

    sourceSets {
        sourceSets["commonMain"].dependencies {
            implementation(project(":shared:data:datastore:api"))
        }

        sourceSets["commonTest"].dependencies {
            implementation(project(":shared:data:datastore:testing"))
        }
    }
}

android {
    namespace = "com.thomaskioko.tvmaniac.domain.settings"
}
