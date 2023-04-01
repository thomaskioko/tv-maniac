plugins {
    id("tvmaniac.kmm.api")
}


kotlin {
    android()
    ios()

    sourceSets {

        sourceSets["commonMain"].dependencies {
            implementation(project(":shared:data:shows:api"))
            implementation(project(":shared:data:tmdb:api"))

        }
    }
}

android {
    namespace = "com.thomaskioko.tvmaniac.data.category.api"
}
