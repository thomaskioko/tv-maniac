plugins {
    alias(libs.plugins.app.kmp)
}

scaffold {
    optIn("kotlinx.coroutines.FlowPreview")
    optIn("kotlinx.coroutines.ExperimentalCoroutinesApi")
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {

                api(projects.data.cast.api)
                api(projects.data.datastore.api)
                api(projects.data.episode.api)
                api(projects.data.seasondetails.api)
                api(projects.data.seasons.api)
                api(projects.data.showdetails.api)
                api(projects.data.shows.api)
                api(projects.data.similar.api)
                api(projects.data.trailers.api)
                api(projects.data.watchlist.api)
                api(projects.data.watchproviders.api)

                implementation(projects.core.base)
                implementation(projects.core.util.api)

                implementation(libs.coroutines.core)
                implementation(libs.kotlinInject.runtime)
            }
        }

        commonTest {
            dependencies {
                implementation(projects.data.featuredshows.testing)
                implementation(projects.data.genre.testing)
                implementation(projects.data.popularshows.testing)
                implementation(projects.data.topratedshows.testing)
                implementation(projects.data.trendingshows.testing)
                implementation(projects.data.upcomingshows.testing)

                implementation(libs.bundles.unittest)
            }
        }
    }
}
