plugins {
    alias(libs.plugins.app.kmp)
}

scaffold {
    useKotlinInject()
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(projects.core.view)

                implementation(projects.core.base)
                implementation(projects.core.logger.api)
                implementation(projects.core.util.api)
                implementation(projects.data.datastore.api)
                implementation(projects.data.traktauth.api)
                implementation(projects.i18n.api)
                implementation(projects.i18n.generator)
                implementation(projects.domain.library)
                implementation(projects.domain.notifications)
                implementation(projects.domain.upnext)

                api(libs.decompose.decompose)
                api(libs.essenty.lifecycle)

                implementation(libs.coroutines.core)
                implementation(libs.kotlinInject.runtime)
            }
        }

        commonTest {
            dependencies {
                implementation(projects.core.logger.testing)
                implementation(projects.core.notifications.testing)
                implementation(projects.core.util.testing)
                implementation(projects.data.datastore.testing)
                implementation(projects.data.episode.testing)
                implementation(projects.data.followedshows.testing)
                implementation(projects.data.library.testing)
                implementation(projects.data.showdetails.testing)
                implementation(projects.data.syncActivity.testing)
                implementation(projects.data.traktauth.testing)
                implementation(projects.data.upnext.testing)
                implementation(projects.data.watchproviders.testing)
                implementation(projects.i18n.testing)

                implementation(libs.bundles.unittest)
            }
        }
    }
}
