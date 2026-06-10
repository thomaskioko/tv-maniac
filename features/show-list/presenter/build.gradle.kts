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
        androidMain.dependencies {
            api(projects.data.traktlists.api)
        }

        commonMain {
            dependencies {
                api(projects.core.base)
                api(projects.core.featureFlags.api)
                api(projects.core.logger.api)
                api(projects.core.view)
                api(projects.data.accountManager.api)
                api(projects.domain.traktlists)
                api(projects.features.showList.nav)
                api(projects.i18n.api)
                api(projects.navigation.api)

                api(libs.decompose.decompose)
                api(libs.essenty.lifecycle)
                api(libs.kotlinx.collections)

                implementation(projects.i18n.generator)
            }
        }

        commonTest {
            dependencies {
                implementation(libs.bundles.unittest)
                implementation(projects.core.base.testing)
                implementation(projects.core.featureFlags.testing)
                implementation(projects.core.logger.testing)
                implementation(projects.data.accountManager.testing)
                implementation(projects.data.traktlists.testing)
                implementation(projects.data.user.testing)
                implementation(projects.i18n.testing)
                implementation(projects.navigation.testing)
            }
        }
    }
}
