plugins {
    id("tvmaniac.kmm.domain")
}


kotlin {
    android()
    ios()

    sourceSets {
        sourceSets["commonMain"].dependencies {
            implementation(project(":shared:data:episodes:api"))
            implementation(project(":shared:data:season-details:api"))
        }

        sourceSets["commonTest"].dependencies {
            implementation(project(":shared:data:episodes:testing"))
            implementation(project(":shared:data:season-details:testing"))
        }
    }
}

dependencies {
    add("kspIosX64", libs.kotlinInject.compiler)
    add("kspIosArm64", libs.kotlinInject.compiler)
}

android {
    namespace = "com.thomaskioko.tvmaniac.data.seasondetails"
}
