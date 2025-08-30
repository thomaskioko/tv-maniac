plugins {
    alias(libs.plugins.app.kmp)
}

scaffold {
    useKotlinInject()

    optIn("kotlinx.coroutines.ExperimentalCoroutinesApi")
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.core.base)
                implementation(projects.data.datastore.api)
                implementation(projects.data.traktauth.api)

                api(libs.decompose.decompose)
                api(libs.essenty.lifecycle)

                implementation(libs.kotlinInject.runtime)
            }
        }

        commonTest {
            dependencies {
                implementation(projects.data.datastore.testing)
                implementation(projects.data.traktauth.testing)

                implementation(libs.bundles.unittest)
            }
        }
    }
}
