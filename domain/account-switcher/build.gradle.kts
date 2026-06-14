plugins {
    alias(libs.plugins.app.kmp)
}

scaffold {
    useMetro()
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(projects.core.base)
                api(projects.data.accountManager.api)
                api(projects.data.episode.api)
                api(projects.data.library.api)
                api(projects.data.logout.api)
                api(projects.data.traktlists.api)
                api(projects.domain.continueWatching)
                api(projects.domain.library)
            }
        }

        commonTest {
            dependencies {
                implementation(libs.bundles.unittest)
                implementation(projects.data.accountManager.testing)
                implementation(projects.data.episode.testing)
                implementation(projects.data.library.testing)
                implementation(projects.data.logout.testing)
                implementation(projects.data.traktlists.testing)
            }
        }
    }
}
