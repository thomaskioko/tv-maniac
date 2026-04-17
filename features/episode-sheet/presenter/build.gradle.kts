plugins {
    alias(libs.plugins.app.kmp)
}

scaffold {
    useMetro()
    useSerialization()
    useCodegen()
    optIn("kotlinx.coroutines.ExperimentalCoroutinesApi")
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(projects.core.view)
                implementation(projects.features.episodeSheet.nav)
                implementation(projects.core.base)
                implementation(projects.navigation.api)
                implementation(projects.core.logger.api)
                implementation(projects.data.episode.api)
                implementation(projects.data.followedshows.api)
                implementation(projects.domain.episode)
                implementation(projects.domain.followedshows)
                implementation(projects.features.root.nav)
                implementation(projects.features.showDetails.nav)
                implementation(projects.features.seasonDetails.nav)

                api(libs.decompose.decompose)
                api(libs.essenty.lifecycle)
                api(libs.kotlinx.collections)
            }
        }

        commonTest {
            dependencies {
                implementation(projects.core.logger.testing)
                implementation(projects.core.util.testing)
                implementation(projects.data.episode.testing)
                implementation(projects.data.followedshows.testing)

                implementation(libs.bundles.unittest)
            }
        }
    }
}
