plugins {
    alias(libs.plugins.app.kmp)
}

scaffold {
    useMetro()
    useCodegen()

    optIn("kotlinx.coroutines.ExperimentalCoroutinesApi")
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.core.base)
                implementation(projects.navigation.api)
                implementation(projects.features.trailers.nav)
                implementation(projects.data.trailers.api)

                api(libs.decompose.decompose)
                api(libs.essenty.lifecycle)
                api(libs.kotlinx.collections)
            }
        }

        commonTest {
            dependencies {
                implementation(projects.data.trailers.testing)

                implementation(libs.bundles.unittest)
            }
        }
    }
}
