plugins {
    alias(libs.plugins.tvmaniac.kmp)
}

tvmaniac {
    useDependencyInjection()
    useSerialization()

    optIn(
        "kotlinx.coroutines.ExperimentalCoroutinesApi",
    )
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(projects.core.view)
                implementation(projects.core.base)
                implementation(projects.core.logger.api)
                implementation(projects.core.util)
                implementation(projects.domain.recommendedshows)
                implementation(projects.domain.showdetails)
                implementation(projects.domain.similarshows)
                implementation(projects.domain.watchproviders)
                implementation(projects.data.watchlist.api)

                api(libs.decompose.decompose)
                api(libs.essenty.lifecycle)
                api(libs.kotlinx.collections)
            }
        }

        commonTest {
            dependencies {
                implementation(projects.core.logger.testing)
                implementation(projects.core.util.testing)
                implementation(projects.data.cast.testing)
                implementation(projects.data.watchlist.testing)
                implementation(projects.data.recommendedshows.testing)
                implementation(projects.data.seasons.testing)
                implementation(projects.data.showdetails.testing)
                implementation(projects.data.similar.testing)
                implementation(projects.data.trailers.testing)
                implementation(projects.data.watchproviders.testing)

                implementation(libs.bundles.unittest)
            }
        }
    }
}
