plugins {
    id("tvmaniac.kmm.domain")
}


kotlin {
    android()
    ios()

    sourceSets {
        sourceSets["commonMain"].dependencies {
            implementation(projects.data.episodeimages.api)
            implementation(projects.data.episodes.api)
            implementation(projects.data.seasondetails.api)
        }

        sourceSets["commonTest"].dependencies {
            implementation(projects.data.episodeimages.testing)
            implementation(projects.data.seasondetails.testing)
        }
    }
}

dependencies {
    add("kspIosX64", libs.kotlinInject.compiler)
    add("kspIosArm64", libs.kotlinInject.compiler)
}

android {
    namespace = "com.thomaskioko.tvmaniac.presentation.seasondetails"
}
