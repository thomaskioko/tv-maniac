plugins {
    id("tvmaniac.kmm.domain")
}

kotlin {
    android()
    ios()

    sourceSets {

        sourceSets["commonMain"].dependencies {
            implementation(project(":shared:data:season-details:api"))
            implementation(project(":shared:data:similar:api"))
            implementation(project(":shared:data:trailers:api"))
            implementation(project(":shared:data:shows:api"))

        }

        sourceSets["commonTest"].dependencies {
            implementation(project(":shared:data:shows:testing"))
            implementation(project(":shared:data:season-details:testing"))
            implementation(project(":shared:data:similar:testing"))
            implementation(project(":shared:data:trailers:testing"))
        }
    }
}

android {
    namespace = "com.thomaskioko.tvmaniac.data.showdetails"
}