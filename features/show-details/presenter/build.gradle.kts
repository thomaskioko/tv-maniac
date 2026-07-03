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
        androidMain {
            dependencies {
                implementation(projects.i18n.generator)
            }
        }

        commonMain {
            dependencies {
                api(projects.core.base)
                api(projects.core.logger.api)
                api(projects.core.notifications.api)
                api(projects.core.view)
                api(projects.data.accountManager.api)
                api(projects.data.episode.api)
                api(projects.data.followedshows.api)
                api(projects.data.seasondetails.api)
                api(projects.domain.episode)
                api(projects.domain.notifications)
                api(projects.domain.ratings)
                api(projects.domain.showdetails)
                api(projects.domain.similarshows)
                api(projects.features.ratingSheet.nav)
                api(projects.features.showDetails.nav)
                api(projects.features.showList.nav)
                api(projects.i18n.api)
                api(projects.navigation.api)

                api(libs.decompose.decompose)
                api(libs.essenty.lifecycle)
                api(libs.kotlinx.collections)

                api(projects.features.root.nav)
                implementation(libs.kotlinx.datetime)
                implementation(projects.features.seasonDetails.nav)
                implementation(projects.features.trailers.nav)
            }
        }

        commonTest {
            dependencies {
                implementation(libs.bundles.unittest)
                implementation(projects.core.base.testing)
                implementation(projects.core.logger.testing)
                implementation(projects.core.notifications.testing)
                implementation(projects.core.util.testing)
                implementation(projects.data.cast.testing)
                implementation(projects.data.accountManager.testing)
                implementation(projects.data.datastore.testing)
                implementation(projects.data.episode.testing)
                implementation(projects.data.followedshows.testing)
                implementation(projects.data.library.testing)
                implementation(projects.data.ratings.testing)
                implementation(projects.data.seasondetails.testing)
                implementation(projects.data.seasons.testing)
                implementation(projects.data.showdetails.testing)
                implementation(projects.data.similar.testing)
                implementation(projects.data.trailers.testing)
                implementation(projects.data.upnext.testing)
                implementation(projects.data.watchproviders.testing)
                implementation(projects.i18n.testing)
                implementation(projects.navigation.testing)
            }
        }
    }
}
