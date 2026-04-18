plugins {
    alias(libs.plugins.app.kmp)
}

scaffold {
    useCodegen()
    optIn(
        "kotlinx.coroutines.ExperimentalCoroutinesApi",
        "kotlinx.coroutines.FlowPreview",
    )
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.core.base)
                implementation(projects.navigation.api)
                implementation(projects.features.genreShows.nav)
                implementation(projects.features.search.nav)
                implementation(projects.features.showDetails.nav)
                implementation(projects.core.util.api)
                implementation(projects.core.logger.api)
                implementation(projects.i18n.api)
                implementation(projects.data.featuredshows.api)
                implementation(projects.data.trendingshows.api)
                implementation(projects.data.upcomingshows.api)
                implementation(projects.data.search.api)
                implementation(projects.data.genre.api)
                implementation(projects.domain.genre)

                api(projects.core.view)
                api(libs.decompose.decompose)
                api(libs.essenty.lifecycle)
                api(libs.kotlinx.collections)

                implementation(libs.coroutines.core)
            }
        }

        commonTest {
            dependencies {
                implementation(projects.core.util.testing)
                implementation(projects.core.logger.testing)
                implementation(projects.i18n.testing)
                implementation(projects.data.search.testing)
                implementation(projects.data.genre.testing)

                implementation(libs.bundles.unittest)
            }
        }
    }
}
