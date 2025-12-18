plugins {
    alias(libs.plugins.app.kmp)
}

scaffold {
    explicitApi()
    optIn(
        "kotlinx.coroutines.ExperimentalCoroutinesApi",
    )
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(projects.data.user.api)
                api(projects.data.traktauth.api)
                api(projects.data.requestManager.api)
                api(projects.core.util)

                implementation(projects.core.base)
                implementation(projects.data.datastore.api)

                implementation(libs.coroutines.core)
                implementation(libs.kotlinInject.runtime)
            }
        }

        commonTest {
            dependencies {
                implementation(projects.data.datastore.testing)
                implementation(projects.data.user.testing)
                implementation(projects.data.traktauth.testing)

                implementation(libs.bundles.unittest)
            }
        }
    }
}
