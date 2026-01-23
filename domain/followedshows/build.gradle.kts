plugins {
    alias(libs.plugins.app.kmp)
}

scaffold {
    useKotlinInject()
    optIn("kotlinx.coroutines.ExperimentalCoroutinesApi")
    optIn("kotlinx.coroutines.FlowPreview")
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(projects.data.followedshows.api)

                implementation(projects.core.base)
                implementation(projects.core.logger.api)
                implementation(projects.core.networkUtil.api)
                implementation(projects.data.datastore.api)
                implementation(projects.data.watchlist.api)
                implementation(projects.data.followedshows.api)
                implementation(projects.data.episode.api)
                implementation(projects.data.seasondetails.api)
                implementation(projects.data.seasons.api)
                implementation(projects.data.syncActivity.api)
                implementation(projects.data.traktauth.api)
                implementation(projects.domain.showdetails)

                implementation(libs.coroutines.core)
                implementation(libs.kotlinInject.runtime)
            }
        }

        commonTest {
            dependencies {
                implementation(libs.bundles.unittest)
                implementation(projects.core.util.testing)
                implementation(projects.data.followedshows.testing)
            }
        }
    }
}
