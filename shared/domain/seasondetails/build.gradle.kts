plugins {
    id("tvmaniac.kmm.domain")
}


kotlin {
    android()
    ios()

    sourceSets {
        sourceSets["commonMain"].dependencies {
            implementation(projects.shared.data.episodes.api)
            implementation(projects.shared.data.seasonDetails.api)
        }

        sourceSets["commonTest"].dependencies {
            implementation(projects.shared.data.episodes.testing)
            implementation(projects.shared.data.seasonDetails.testing)
        }
    }
}

dependencies {
    add("kspIosX64", libs.kotlinInject.compiler)
    add("kspIosArm64", libs.kotlinInject.compiler)
}

android {
    namespace = "com.thomaskioko.tvmaniac.domain.seasondetails"
}
