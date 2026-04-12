plugins {
    alias(libs.plugins.app.kmp)
}

scaffold {
    useMetro()

    optIn("kotlinx.coroutines.FlowPreview")
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.core.base)
                implementation(projects.core.logger.api)
                implementation(projects.core.view)
                implementation(projects.data.datastore.api)
                implementation(projects.data.traktauth.api)
                implementation(projects.domain.logout)
                implementation(projects.domain.user)
                implementation(projects.navigation.api)
                implementation(projects.features.root.nav)
                implementation(projects.features.seasonDetails.nav.api)
                implementation(projects.features.showDetails.nav.api)

                implementation(projects.features.calendar.presenter)
                implementation(projects.features.debug.presenter)
                implementation(projects.features.discover.presenter)
                implementation(projects.features.episodeDetail.presenter)
                implementation(projects.features.home.presenter)
                implementation(projects.features.library.presenter)
                implementation(projects.features.moreShows.presenter)
                implementation(projects.features.profile.presenter)
                implementation(projects.features.progress.presenter)
                implementation(projects.features.search.presenter)
                implementation(projects.features.seasonDetails.presenter)
                implementation(projects.features.settings.presenter)
                implementation(projects.features.showDetails.presenter)
                implementation(projects.features.trailers.presenter)
                implementation(projects.features.upnext.presenter)
                implementation(projects.features.watchlist.presenter)

                api(libs.decompose.decompose)
                api(libs.essenty.lifecycle)
                api(libs.coroutines.core)
            }
        }

        commonTest {
            dependencies {
                implementation(projects.core.testing.di)
                implementation(projects.navigation.implementation)
                implementation(projects.data.traktauth.testing)
                implementation(projects.data.datastore.testing)

                implementation(libs.bundles.unittest)
            }
        }
    }
}
