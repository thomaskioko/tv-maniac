plugins {
    alias(libs.plugins.app.kmp)
}

scaffold {
    useMetro()
    optIn("kotlinx.coroutines.FlowPreview")
    optIn("kotlinx.coroutines.ExperimentalCoroutinesApi")
}

kotlin {
    sourceSets {
        androidMain {
            dependencies {
                implementation(projects.data.database.sqldelight)
            }
        }

        commonMain {
            dependencies {
                api(libs.coroutines.core)
                api(projects.core.base)
                api(projects.core.syncstate.api)
                api(projects.core.util.api)
                api(projects.data.cast.api)
                api(projects.data.episode.api)
                api(projects.data.followedshows.api)
                api(projects.data.seasondetails.api)
                api(projects.data.seasons.api)
                api(projects.data.showdetails.api)
                api(projects.data.similar.api)
                api(projects.data.trailers.api)
                api(projects.data.watchproviders.api)
            }
        }

        commonTest {
            dependencies {
                implementation(libs.bundles.unittest)
            }
        }
    }
}
