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
                api(projects.core.util.api)
                api(projects.core.view)
                api(projects.data.episode.api)
                api(projects.data.ratings.api)
                api(projects.domain.episode)
                api(projects.domain.ratings)
                api(projects.features.ratingSheet.nav)
                api(projects.features.watchdateSelection.nav)
                api(projects.i18n.api)
                api(projects.navigation.api)

                api(libs.decompose.decompose)
                api(libs.essenty.lifecycle)

                implementation(projects.i18n.generator)
            }
        }

        commonTest {
            dependencies {
                implementation(libs.bundles.unittest)
                implementation(projects.core.base.testing)
                implementation(projects.core.logger.testing)
                implementation(projects.core.util.testing)
                implementation(projects.data.episode.testing)
                implementation(projects.data.ratings.testing)
                implementation(projects.data.rewatch.testing)
                implementation(projects.data.datastore.testing)
                implementation(projects.data.subscription.testing)
                implementation(projects.i18n.testing)
                implementation(projects.navigation.testing)
            }
        }
    }
}
