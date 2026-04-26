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
                api(projects.core.view)

                implementation(projects.core.appconfig.api)
                implementation(projects.core.base)
                implementation(projects.navigation.api)
                implementation(projects.core.logger.api)
                implementation(projects.features.debug.nav)
                implementation(projects.features.settings.nav)
                implementation(projects.core.util.api)
                implementation(projects.domain.theme)
                implementation(projects.data.datastore.api)
                implementation(projects.data.traktauth.api)
                implementation(projects.domain.logout)
                implementation(projects.domain.notifications)
                implementation(projects.domain.settings)
                implementation(projects.domain.user)

                api(libs.decompose.decompose)
                api(libs.essenty.lifecycle)
            }
        }

        commonTest {
            dependencies {
                implementation(projects.core.tasks.api)
                implementation(projects.core.util.testing)
                implementation(projects.data.datastore.testing)
                implementation(projects.data.traktauth.testing)
                implementation(projects.data.user.testing)
                implementation(projects.core.logger.testing)
                implementation(projects.core.util.testing)
                implementation(projects.i18n.testing)
                implementation(projects.data.syncActivity.testing)

                implementation(libs.bundles.unittest)
            }
        }
    }
}
