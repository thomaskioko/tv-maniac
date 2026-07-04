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
                api(projects.core.base)
                api(projects.core.logger.api)
                api(projects.core.view)
                api(projects.domain.episode)
                api(projects.domain.followedshows)
                api(projects.domain.ratings)
                api(projects.features.episodeSheet.nav)
                api(projects.features.ratingSheet.nav)
                api(projects.i18n.api)
                api(projects.navigation.api)

                api(libs.decompose.decompose)
                api(libs.essenty.lifecycle)
                api(libs.kotlinx.collections)

                implementation(projects.features.showDetails.nav)
                implementation(projects.features.seasonDetails.nav)
                implementation(projects.i18n.generator)
            }
        }

        commonTest {
            dependencies {
                implementation(libs.bundles.unittest)
                implementation(projects.core.base.testing)
                implementation(projects.core.logger.testing)
                implementation(projects.data.database.sqldelight)
                implementation(projects.data.episode.testing)
                implementation(projects.data.ratings.testing)
                implementation(projects.data.followedshows.testing)
                implementation(projects.data.library.testing)
                implementation(projects.i18n.testing)
                implementation(projects.navigation.testing)
            }
        }
    }
}
