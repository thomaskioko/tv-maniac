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
                api(projects.core.featureFlags.api)
                api(projects.core.logger.api)
                api(projects.core.util.api)
                api(projects.core.view)
                api(projects.data.accountManager.api)
                api(projects.data.datastore.api)
                api(projects.data.user.api)
                api(projects.domain.accountSwitcher)
                api(projects.domain.logout)
                api(projects.domain.notifications)
                api(projects.domain.settings)
                api(projects.domain.theme)
                api(projects.core.appconfig.api)
                api(projects.features.settings.nav)
                api(projects.i18n.api)
                api(projects.navigation.api)
                api(projects.i18n.generator)

                api(libs.decompose.decompose)
                api(libs.essenty.lifecycle)
                api(libs.kotlinx.collections)

                implementation(projects.features.debug.nav)
            }
        }

        commonTest {
            dependencies {
                implementation(libs.bundles.unittest)
                implementation(projects.core.base.testing)
                implementation(projects.core.featureFlags.testing)
                implementation(projects.core.logger.testing)
                implementation(projects.core.util.testing)
                implementation(projects.data.accountManager.testing)
                implementation(projects.data.datastore.testing)
                implementation(projects.data.episode.testing)
                implementation(projects.data.library.testing)
                implementation(projects.data.traktlists.testing)
                implementation(projects.data.logout.testing)
                implementation(projects.data.user.testing)
                implementation(projects.i18n.testing)
                implementation(projects.navigation.testing)
            }
        }
    }
}
