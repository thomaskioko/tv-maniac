plugins {
    alias(libs.plugins.app.kmp)
}

scaffold {
    useMetro()
    optIn(
        "kotlinx.coroutines.ExperimentalCoroutinesApi",
    )
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(libs.coroutines.core)
                api(projects.core.base)
                api(projects.data.featuredshows.api)
                api(projects.data.genre.api)
                api(projects.data.popularshows.api)
                api(projects.data.shows.api)
                api(projects.data.topratedshows.api)
                api(projects.data.trendingshows.api)
                api(projects.data.upcomingshows.api)
                api(projects.data.upnext.api)
                api(projects.domain.upnext)
            }
        }

        commonTest {
            dependencies {
                implementation(libs.bundles.unittest)
                implementation(projects.data.featuredshows.testing)
                implementation(projects.data.genre.testing)
                implementation(projects.data.popularshows.testing)
                implementation(projects.data.topratedshows.testing)
                implementation(projects.data.trendingshows.testing)
                implementation(projects.data.upcomingshows.testing)
                implementation(projects.data.upnext.testing)
            }
        }
    }
}
