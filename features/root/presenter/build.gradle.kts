plugins {
    alias(libs.plugins.app.kmp)
}

scaffold {
    useMetro()

    optIn("kotlinx.coroutines.FlowPreview")
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.core.base)
                implementation(projects.core.logger.api)
                implementation(projects.core.view)
                implementation(projects.data.datastore.api)
                implementation(projects.domain.theme)
                implementation(projects.features.settings.presenter)
                implementation(projects.domain.logout)
                implementation(projects.domain.user)
                implementation(projects.navigation.api)
                implementation(projects.features.root.nav)
                implementation(projects.features.debug.nav)
                implementation(projects.features.home.nav)
                implementation(projects.features.seasonDetails.nav)
                implementation(projects.features.showDetails.nav)

                api(libs.decompose.decompose)
                api(libs.essenty.lifecycle)
                api(libs.coroutines.core)
            }
        }

        commonTest {
            dependencies {
                implementation(projects.core.testing.di)
                implementation(projects.navigation.implementation)
                implementation(projects.data.traktauth.testing)
                implementation(projects.data.datastore.testing)
                implementation(projects.features.genreShows.nav)
                implementation(projects.features.moreShows.nav)
                implementation(projects.features.trailers.nav)

                implementation(libs.bundles.unittest)
            }
        }
    }
}
