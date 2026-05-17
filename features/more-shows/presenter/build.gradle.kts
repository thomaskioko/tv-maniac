plugins {
    alias(libs.plugins.app.kmp)
}

scaffold {
    useCodegen()
}

kotlin {
    sourceSets {
        androidMain {
            dependencies {
                implementation(projects.data.shows.api)
            }
        }

        commonMain {
            dependencies {
                api(projects.core.base)
                api(projects.data.popularshows.api)
                api(projects.data.topratedshows.api)
                api(projects.data.trendingshows.api)
                api(projects.data.upcomingshows.api)
                api(projects.features.moreShows.nav)
                api(projects.navigation.api)

                api(libs.decompose.decompose)
                api(libs.essenty.lifecycle)
                api(libs.kotlinx.collections)

                implementation(projects.features.showDetails.nav)
            }
        }

        commonTest { dependencies { implementation(libs.bundles.unittest) } }
    }
}
