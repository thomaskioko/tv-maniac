plugins {
    alias(libs.plugins.app.kmp)
}

scaffold {
    useCodegen()

    optIn("kotlinx.coroutines.FlowPreview")
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
                api(projects.core.syncstate.api)
                api(projects.data.connectedAccount.api)
                api(projects.data.datastore.api)
                api(projects.data.traktauth.api)
                api(projects.domain.logout)
                api(projects.domain.user)
                api(projects.features.home.presenter)
                api(projects.features.root.nav)
                api(projects.i18n.api)
                api(projects.navigation.api)

                api(libs.decompose.decompose)
                api(libs.essenty.lifecycle)
                api(libs.coroutines.core)

                implementation(projects.core.view)
                implementation(projects.domain.theme)
                implementation(projects.features.debug.nav)
                implementation(projects.features.seasonDetails.nav)
                implementation(projects.features.settings.presenter)
                implementation(projects.features.showDetails.nav)
            }
        }

        commonTest {
            dependencies {
                implementation(libs.bundles.unittest)
                implementation(projects.core.integration.infra)
                implementation(projects.features.genreShows.nav)
                implementation(projects.features.moreShows.nav)
                implementation(projects.features.trailers.nav)
                implementation(projects.i18n.testing)
            }
        }
    }
}
