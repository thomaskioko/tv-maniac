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
            implementation(projects.features.root.nav)
            implementation(projects.core.base)
            implementation(projects.navigation.api)

            implementation(projects.features.episodeSheet.nav.api)
            implementation(projects.features.home.nav.api)
            implementation(projects.features.showDetails.nav.api)
            implementation(projects.features.seasonDetails.nav.api)

            implementation(libs.coroutines.core)
        }

        commonTest.dependencies {
            implementation(projects.core.util.testing)
            implementation(projects.data.traktauth.testing)
            implementation(projects.core.testing.di)
            implementation(projects.features.genreShows.nav.api)

            implementation(libs.bundles.unittest)
        }
    }
}
