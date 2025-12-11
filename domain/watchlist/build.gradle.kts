plugins {
    alias(libs.plugins.app.kmp)
}

scaffold {
    optIn("kotlinx.coroutines.ExperimentalCoroutinesApi")
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(projects.data.shows.api)

                implementation(projects.core.base)
                implementation(projects.core.util.api)
                implementation(projects.core.networkUtil)
                implementation(projects.data.watchlist.api)

                implementation(libs.coroutines.core)
                implementation(libs.kotlinInject.runtime)
            }
        }

        commonTest {
            dependencies {
                implementation(libs.bundles.unittest)
                implementation(projects.data.watchlist.testing)
            }
        }
    }
}
