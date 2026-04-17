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
            implementation(projects.data.datastore.api)

            implementation(projects.features.episodeSheet.nav)
            implementation(projects.features.home.nav)
            implementation(projects.features.showDetails.nav)
            implementation(projects.features.seasonDetails.nav)

            implementation(libs.coroutines.core)
        }

        commonTest.dependencies {
            implementation(projects.core.util.testing)
            implementation(projects.data.traktauth.testing)
            implementation(projects.core.testing.di)
            implementation(projects.features.genreShows.nav)

            implementation(libs.bundles.unittest)
        }
    }
}
