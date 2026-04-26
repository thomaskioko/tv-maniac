plugins {
    alias(libs.plugins.app.kmp)
}

scaffold {
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
                implementation(projects.features.showDetails.nav)
                implementation(projects.features.seasonDetails.nav)
                implementation(projects.i18n.api)

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
                implementation(projects.i18n.testing)
                implementation(projects.navigation.testing)

                implementation(libs.bundles.unittest)
            }
        }
    }
}
