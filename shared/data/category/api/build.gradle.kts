plugins {
    id("tvmaniac.kmm.library")
}


kotlin {
    android()
    ios()

    sourceSets {

        sourceSets["commonMain"].dependencies {
            api(project(":shared:networkutil"))
            api(project(":shared:data:database"))
            implementation(project(":shared:data:shows:api"))
            implementation(project(":shared:data:tmdb:api"))

            api(libs.coroutines.core)

        }
    }
}

android {
    namespace = "com.thomaskioko.tvmaniac.data.category.api"
}
