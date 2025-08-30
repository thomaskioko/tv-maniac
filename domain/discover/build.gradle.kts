plugins {
    alias(libs.plugins.app.kmp)
}

scaffold {
    optIn(
        "kotlinx.coroutines.ExperimentalCoroutinesApi",
    )
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {

                api(projects.data.featuredshows.api)
                api(projects.data.genre.api)
                api(projects.data.popularshows.api)
                api(projects.data.topratedshows.api)
                api(projects.data.trendingshows.api)
                api(projects.data.upcomingshows.api)

                implementation(projects.core.base)

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
