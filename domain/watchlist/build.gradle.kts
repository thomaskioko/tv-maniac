plugins {
    alias(libs.plugins.app.kmp)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(projects.data.shows.api)

                implementation(projects.core.base)
                implementation(projects.core.util.api)
                implementation(projects.data.episode.api)
                implementation(projects.data.library.api)
                implementation(projects.data.watchlist.api)
                implementation(projects.data.syncActivity.api)

                implementation(libs.coroutines.core)
                implementation(libs.kotlinInject.runtime)
            }
        }

        commonTest {
            dependencies {
                implementation(libs.bundles.unittest)
                implementation(projects.core.util.testing)
                implementation(projects.data.episode.testing)
                implementation(projects.data.library.testing)
                implementation(projects.data.watchlist.testing)
            }
        }
    }
}
