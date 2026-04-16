plugins {
    alias(libs.plugins.app.kmp)
}

scaffold {
    useMetro()
    useSerialization()

    optIn("kotlinx.coroutines.ExperimentalCoroutinesApi")
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(projects.core.view)
                implementation(projects.core.base)
                implementation(projects.navigation.api)
                implementation(projects.features.root.nav)
                implementation(projects.features.seasonDetails.nav)
                implementation(projects.core.logger.api)
                implementation(projects.data.episode.api)
                implementation(projects.data.seasondetails.api)
                implementation(projects.domain.episode)
                implementation(projects.domain.seasondetails)

                api(libs.decompose.decompose)
                api(libs.essenty.lifecycle)
                api(libs.kotlinx.collections)
            }
        }

        commonTest {
            dependencies {
                implementation(projects.core.logger.testing)
                implementation(projects.data.seasondetails.testing)
                implementation(projects.data.cast.testing)
                implementation(projects.data.episode.testing)

                implementation(libs.bundles.unittest)
            }
        }
    }
}
