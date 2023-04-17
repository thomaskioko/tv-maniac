plugins {
    id("tvmaniac.kmm.domain")
}

kotlin {
    android()
    ios()

    sourceSets {

        sourceSets["commonMain"].dependencies {
            implementation(project(":shared:util"))
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

dependencies {
    add("kspIosX64", libs.kotlinInject.compiler)
    add("kspIosArm64", libs.kotlinInject.compiler)
}

android {
    namespace = "com.thomaskioko.tvmaniac.data.showdetails"
}