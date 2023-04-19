plugins {
    id("tvmaniac.kmm.domain")
}

kotlin {
    android()
    ios()

    sourceSets {
        sourceSets["commonMain"].dependencies {
            implementation(projects.shared.core.tmdbApi.api)
            implementation(projects.shared.data.category.api)
            implementation(projects.shared.data.shows.api)
        }

        sourceSets["commonTest"].dependencies {

            implementation(projects.shared.core.tmdbApi.testing)
            implementation(projects.shared.data.shows.testing)
        }
    }
}

dependencies {
    add("kspIosX64", libs.kotlinInject.compiler)
    add("kspIosArm64", libs.kotlinInject.compiler)
}

android {
    namespace = "com.thomaskioko.tvmaniac.domain.discover"
}
