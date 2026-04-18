plugins {
    alias(libs.plugins.app.kmp)
}

scaffold {
    useCodegen()
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.core.base)
                implementation(projects.navigation.api)
                implementation(projects.features.moreShows.nav)
                implementation(projects.features.showDetails.nav)
                implementation(projects.data.popularshows.api)
                implementation(projects.data.topratedshows.api)
                implementation(projects.data.trendingshows.api)
                implementation(projects.data.upcomingshows.api)

                api(libs.decompose.decompose)
                api(libs.essenty.lifecycle)
                api(libs.kotlinx.collections)
            }
        }

        commonTest { dependencies { implementation(libs.bundles.unittest) } }
    }
}
