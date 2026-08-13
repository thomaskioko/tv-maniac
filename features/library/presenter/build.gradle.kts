plugins {
    alias(libs.plugins.app.kmp)
}

scaffold {
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
                api(projects.data.accountManager.api)
                api(projects.data.library.api)
                api(projects.data.subscription.api)
                api(projects.domain.library)
                api(projects.features.library.nav)
                api(projects.navigation.api)

                api(libs.decompose.decompose)
                api(libs.essenty.lifecycle)
                api(libs.kotlinx.collections)

                implementation(projects.features.home.nav)
                implementation(projects.features.showDetails.nav)
            }
        }

        commonTest {
            dependencies {
                implementation(libs.bundles.unittest)
                implementation(projects.core.logger.testing)
                implementation(projects.core.syncstate.testing)
                implementation(projects.core.util.testing)
                implementation(projects.data.accountManager.testing)
                implementation(projects.data.datastore.testing)
                implementation(projects.data.episode.testing)
                implementation(projects.data.followedshows.testing)
                implementation(projects.data.library.testing)
                implementation(projects.data.seasondetails.testing)
                implementation(projects.data.showdetails.testing)
                implementation(projects.data.subscription.testing)
                implementation(projects.data.syncActivity.testing)
                implementation(projects.data.watchproviders.testing)
                implementation(projects.navigation.testing)
            }
        }
    }
}
