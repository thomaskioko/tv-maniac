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
                api(projects.core.view)
                api(projects.data.accountManager.api)
                api(projects.data.library.api)
                api(projects.domain.library)
                api(projects.features.library.nav)
                api(projects.navigation.api)

                api(libs.decompose.decompose)
                api(libs.essenty.lifecycle)
                api(libs.kotlinx.collections)

                implementation(projects.features.home.nav)
                implementation(projects.features.showDetails.nav)
            }
        }

        commonTest {
            dependencies {
                implementation(libs.bundles.unittest)
            }
        }
    }
}
