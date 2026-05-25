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
            api(libs.coroutines.core)
            api(projects.core.base)
            api(projects.navigation.api)
            implementation(projects.features.home.nav)
        }

        commonTest.dependencies {
            implementation(libs.bundles.unittest)
            implementation(libs.kotlinx.serialization.json)
            implementation(projects.features.discover.nav)
            implementation(projects.features.genreShows.nav)
            implementation(projects.features.home.nav)
            implementation(projects.features.library.nav)
            implementation(projects.features.moreShows.nav)
            implementation(projects.features.seasonDetails.nav)
            implementation(projects.features.showDetails.nav)
            implementation(projects.features.myShows.nav)
            implementation(projects.navigation.implementation)
        }
    }
}
