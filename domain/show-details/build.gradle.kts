plugins {
    id("tvmaniac.kmm.domain")
}

kotlin {
    android()
    ios()

    sourceSets {

        sourceSets["commonMain"].dependencies {
            implementation(projects.core.util)
            implementation(projects.data.seasonDetails.api)
            implementation(projects.data.similar.api)
            implementation(projects.data.trailers.api)
            implementation(projects.data.shows.api)

        }

        sourceSets["commonTest"].dependencies {
            implementation(projects.data.shows.testing)
            implementation(projects.data.seasonDetails.testing)
            implementation(projects.data.similar.testing)
            implementation(projects.data.trailers.testing)
        }
    }
}

dependencies {
    add("kspIosX64", libs.kotlinInject.compiler)
    add("kspIosArm64", libs.kotlinInject.compiler)
}

android {
    namespace = "com.thomaskioko.tvmaniac.domain.showdetails"
}