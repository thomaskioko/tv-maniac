plugins {
    id("tvmaniac.kmm.domain")
}

kotlin {
    android()
    ios()

    sourceSets {
        sourceSets["commonMain"].dependencies {
            implementation(project(":shared:data:category:api"))
            implementation(project(":shared:data:shows:api"))
            implementation(project(":shared:data:tmdb:api"))
        }

        sourceSets["commonTest"].dependencies {

            implementation(project(":shared:data:shows:testing"))
            implementation(project(":shared:data:tmdb:testing"))
        }
    }
}

dependencies {
    add("kspIosX64", libs.kotlinInject.compiler)
    add("kspIosArm64", libs.kotlinInject.compiler)
}

android {
    namespace = "com.thomaskioko.tvmaniac.discover"
}
