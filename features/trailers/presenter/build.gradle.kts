plugins {
    alias(libs.plugins.app.kmp)
}

scaffold {
    useCodegen()

    optIn("kotlinx.coroutines.ExperimentalCoroutinesApi")
}

kotlin {
    sourceSets {
        androidMain {
            dependencies {
                implementation(projects.data.database.sqldelight)
            }
        }

        commonMain {
            dependencies {
                api(projects.core.base)
                api(projects.data.trailers.api)
                api(projects.features.trailers.nav)
                api(projects.navigation.api)

                api(libs.decompose.decompose)
                api(libs.essenty.lifecycle)
                api(libs.kotlinx.collections)
            }
        }

        commonTest {
            dependencies {
                implementation(libs.bundles.unittest)
                implementation(projects.data.trailers.testing)
            }
        }
    }
}
