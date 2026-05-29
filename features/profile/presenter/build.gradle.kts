plugins {
    alias(libs.plugins.app.kmp)
}

scaffold {
    useCodegen()

    optIn("kotlinx.coroutines.ExperimentalCoroutinesApi")
}

kotlin {
    sourceSets {
        androidMain {
            dependencies {
                implementation(projects.data.episode.api)
                implementation(projects.data.favorites.api)
                implementation(projects.data.library.api)
                implementation(projects.data.upnext.api)
            }
        }

        commonMain {
            dependencies {
                api(projects.core.base)
                api(projects.core.logger.api)
                api(projects.core.view)
                api(projects.data.traktauth.api)
                api(projects.domain.continueWatching)
                api(projects.domain.favorites)
                api(projects.domain.library)
                api(projects.domain.recentlyWatched)
                api(projects.domain.traktlists)
                api(projects.domain.user)
                api(projects.features.profile.nav)
                api(projects.i18n.api)
                api(projects.navigation.api)

                api(libs.decompose.decompose)
                api(libs.essenty.lifecycle)

                implementation(projects.data.user.api)
                implementation(projects.features.home.nav)
                implementation(projects.features.settings.nav)
                implementation(projects.features.showDetails.nav)
            }
        }

        commonTest {
            dependencies {
                implementation(libs.bundles.unittest)
                implementation(projects.core.logger.testing)
                implementation(projects.data.episode.testing)
                implementation(projects.data.favorites.testing)
                implementation(projects.data.library.testing)
                implementation(projects.data.traktauth.testing)
                implementation(projects.data.traktlists.api)
                implementation(projects.data.traktlists.testing)
                implementation(projects.data.upnext.testing)
                implementation(projects.data.user.testing)
                implementation(projects.i18n.generator)
                implementation(projects.i18n.testing)
                implementation(projects.navigation.testing)
            }
        }
    }
}
