plugins {
    id("tvmaniac.kmm.domain")
}

kotlin {
    android()
    ios()

    sourceSets {

        sourceSets["commonMain"].dependencies {
            implementation(projects.core.util)
            implementation(projects.data.seasons.api)
            implementation(projects.data.similar.api)
            implementation(projects.data.trailers.api)
            implementation(projects.data.shows.api)
            implementation(projects.data.watchlist.api)

        }

        sourceSets["commonTest"].dependencies {
            implementation(projects.data.shows.testing)
            implementation(projects.data.seasons.testing)
            implementation(projects.data.similar.testing)
            implementation(projects.data.trailers.testing)
            implementation(projects.data.watchlist.testing)
        }
    }
}

dependencies {
    add("kspIosX64", libs.kotlinInject.compiler)
    add("kspIosArm64", libs.kotlinInject.compiler)
}

android {
    namespace = "com.thomaskioko.tvmaniac.presentation.showdetails"
}