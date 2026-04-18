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
                implementation(projects.core.base)
                implementation(projects.core.logger.api)
                implementation(projects.navigation.api)
                implementation(projects.features.home.nav)
                implementation(projects.features.profile.nav)
                implementation(projects.features.settings.nav)
                implementation(projects.i18n.api)
                implementation(projects.domain.user)
                implementation(projects.data.traktauth.api)
                implementation(projects.data.user.api)

                api(libs.decompose.decompose)
                api(libs.essenty.lifecycle)
            }
        }

        commonTest {
            dependencies {
                implementation(projects.core.logger.testing)
                implementation(projects.core.util.testing)
                implementation(projects.data.traktauth.testing)
                implementation(projects.data.traktlists.testing)
                implementation(projects.data.user.testing)
                implementation(projects.i18n.testing)

                implementation(libs.bundles.unittest)
            }
        }
    }
}
