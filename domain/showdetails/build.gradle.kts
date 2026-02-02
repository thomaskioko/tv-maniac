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

                implementation(projects.data.cast.api)
                implementation(projects.data.episode.api)
                implementation(projects.data.seasondetails.api)
                implementation(projects.data.seasons.api)
                implementation(projects.data.showdetails.api)
                implementation(projects.data.shows.api)
                implementation(projects.data.similar.api)
                implementation(projects.data.trailers.api)
                implementation(projects.data.watchlist.api)
                implementation(projects.data.watchproviders.api)
                implementation(projects.core.logger.api)

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
