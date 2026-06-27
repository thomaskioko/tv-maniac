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
                api(projects.data.episode.api)
                api(projects.data.seasondetails.api)
                api(projects.domain.episode)
                api(projects.domain.seasondetails)
                api(projects.features.seasonDetails.nav)
                api(projects.navigation.api)

                api(libs.decompose.decompose)
                api(libs.essenty.lifecycle)
                api(libs.kotlinx.collections)

                implementation(projects.features.episodeSheet.nav)
            }
        }

        commonTest {
            dependencies {
                implementation(libs.bundles.unittest)
                implementation(libs.kotlinx.datetime)
                implementation(projects.core.logger.testing)
                implementation(projects.data.cast.testing)
                implementation(projects.data.database.sqldelight)
                implementation(projects.data.episode.testing)
                implementation(projects.data.seasondetails.testing)
                implementation(projects.navigation.testing)
            }
        }
    }
}
