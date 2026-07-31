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
                api(projects.data.trailers.api)
                api(projects.features.trailers.nav)
                api(projects.i18n.api)
                api(projects.navigation.api)

                implementation(projects.i18n.generator)

                api(libs.decompose.decompose)
                api(libs.essenty.lifecycle)
                api(libs.kotlinx.collections)
            }
        }

        commonTest {
            dependencies {
                implementation(libs.bundles.unittest)
                implementation(projects.data.trailers.testing)
                implementation(projects.i18n.testing)
            }
        }
    }
}
