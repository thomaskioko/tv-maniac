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
            implementation(project(":shared:data:tmdb:api"))

        }
    }
}

android {
    namespace = "com.thomaskioko.tvmaniac.data.category.api"
}
