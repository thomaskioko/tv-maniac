plugins {
    alias(libs.plugins.app.kmp)
}

scaffold {
    useCodegen()
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(libs.coroutines.core)
                api(projects.core.base)
                api(projects.core.logger.api)
                api(projects.core.util.api)
                api(projects.core.view)
                api(projects.data.datastore.api)
                api(projects.data.traktauth.api)
                api(projects.domain.library)
                api(projects.domain.notifications)
                api(projects.domain.upnext)
                api(projects.features.debug.nav)
                api(projects.features.featureFlags.nav)
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
                implementation(libs.kotlinx.datetime)
                implementation(projects.core.logger.testing)
                implementation(projects.core.notifications.api)
                implementation(projects.core.notifications.testing)
                implementation(projects.core.util.testing)
                implementation(projects.data.datastore.testing)
                implementation(projects.data.episode.api)
                implementation(projects.data.episode.testing)
                implementation(projects.data.followedshows.api)
                implementation(projects.data.followedshows.testing)
                implementation(projects.data.library.api)
                implementation(projects.data.library.testing)
                implementation(projects.data.showdetails.api)
                implementation(projects.data.showdetails.testing)
                implementation(projects.data.syncActivity.api)
                implementation(projects.data.syncActivity.testing)
                implementation(projects.data.traktauth.testing)
                implementation(projects.data.upnext.api)
                implementation(projects.data.upnext.testing)
                implementation(projects.data.watchproviders.api)
                implementation(projects.data.watchproviders.testing)
                implementation(projects.i18n.testing)
                implementation(projects.navigation.testing)
            }
        }
    }
}
