plugins {
    alias(libs.plugins.app.kmp)
}

scaffold {
    useMetro()
    useSerialization()

    optIn("kotlinx.coroutines.ExperimentalCoroutinesApi")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.base)
            implementation(projects.navigation.api)

            implementation(libs.coroutines.core)
        }

        commonTest.dependencies {
            implementation(projects.core.util.testing)
            implementation(projects.data.traktauth.testing)
            implementation(projects.core.testing.di)
            implementation(projects.features.episodeSheet.nav)
            implementation(projects.features.genreShows.nav)
            implementation(projects.features.home.nav)
            implementation(projects.features.seasonDetails.nav)
            implementation(projects.features.showDetails.nav)

            implementation(libs.bundles.unittest)
        }
    }
}
