plugins {
    id("tvmaniac.kmm.domain")
}

kotlin {
    android()
    ios()

    sourceSets {

        sourceSets["commonMain"].dependencies {
            implementation(projects.shared.core.util)
            implementation(projects.shared.data.seasonDetails.api)
            implementation(projects.shared.data.similar.api)
            implementation(projects.shared.data.trailers.api)
            implementation(projects.shared.data.shows.api)

        }

        sourceSets["commonTest"].dependencies {
            implementation(projects.shared.data.shows.testing)
            implementation(projects.shared.data.seasonDetails.testing)
            implementation(projects.shared.data.similar.testing)
            implementation(projects.shared.data.trailers.testing)
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