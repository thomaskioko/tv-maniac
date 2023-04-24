plugins {
    id("tvmaniac.kmm.domain")
}

kotlin {
    android()
    ios()

    sourceSets {
        sourceSets["commonMain"].dependencies {
            api(projects.shared.data.category.api)
            implementation(projects.shared.data.shows.api)
            implementation(projects.shared.core.tmdbApi.api)
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
