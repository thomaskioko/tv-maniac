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
                api(projects.core.util.api)
                api(projects.core.view)
                api(projects.data.datastore.api)
                api(projects.data.traktauth.api)
                api(projects.domain.logout)
                api(projects.domain.notifications)
                api(projects.domain.settings)
                api(projects.domain.theme)
                api(projects.core.appconfig.api)
                api(projects.features.settings.nav)
                api(projects.i18n.api)
                api(projects.navigation.api)

                api(libs.decompose.decompose)
                api(libs.essenty.lifecycle)
                api(libs.kotlinx.collections)

                implementation(projects.features.debug.nav)
                implementation(projects.i18n.generator)
            }
        }

        commonTest {
            dependencies {
                implementation(libs.bundles.unittest)
                implementation(projects.core.logger.testing)
                implementation(projects.core.util.testing)
                implementation(projects.data.datastore.testing)
                implementation(projects.data.requestManager.testing)
                implementation(projects.data.syncActivity.api)
                implementation(projects.data.syncActivity.testing)
                implementation(projects.data.traktauth.testing)
                implementation(projects.data.user.api)
                implementation(projects.data.user.testing)
                implementation(projects.i18n.testing)
                implementation(projects.navigation.testing)
            }
        }
    }
}
