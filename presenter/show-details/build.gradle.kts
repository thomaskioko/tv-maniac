plugins {
    alias(libs.plugins.app.kmp)
}

scaffold {
    useKotlinInject()
    useSerialization()

    optIn("kotlinx.coroutines.ExperimentalCoroutinesApi")
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(projects.core.view)
                implementation(projects.core.base)
                implementation(projects.core.logger.api)
                implementation(projects.core.util.api)
                implementation(projects.data.episode.api)
                implementation(projects.domain.episode)
                implementation(projects.domain.seasondetails)
                implementation(projects.domain.showdetails)
                implementation(projects.domain.similarshows)
                implementation(projects.domain.watchproviders)
                implementation(projects.data.followedshows.api)
                implementation(projects.data.seasondetails.api)
                implementation(projects.data.traktauth.api)

                api(libs.decompose.decompose)
                api(libs.essenty.lifecycle)
                api(libs.kotlinx.collections)

                implementation(libs.kotlinInject.runtime)
                implementation(libs.kotlinx.datetime)
            }
        }

        commonTest {
            dependencies {
                implementation(projects.core.logger.testing)
                implementation(projects.core.util.testing)
                implementation(projects.data.cast.testing)
                implementation(projects.data.datastore.testing)
                implementation(projects.data.episode.testing)
                implementation(projects.data.seasondetails.testing)
                implementation(projects.data.followedshows.testing)
                implementation(projects.data.seasons.testing)
                implementation(projects.data.showdetails.testing)
                implementation(projects.data.similar.testing)
                implementation(projects.data.trailers.testing)
                implementation(projects.data.traktauth.api)
                implementation(projects.data.traktauth.testing)
                implementation(projects.data.watchproviders.testing)
                implementation(projects.i18n.testing)

                implementation(libs.bundles.unittest)
            }
        }
    }
}
