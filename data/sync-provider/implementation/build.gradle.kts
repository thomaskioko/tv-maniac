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
                api(projects.api.trakt.api)
                api(projects.core.networkUtil.api)
                api(projects.data.syncActivity.api)
                api(projects.data.syncProvider.api)
            }
        }

        commonTest {
            dependencies {
                implementation(libs.bundles.unittest)
                implementation(projects.api.trakt.testing)
            }
        }
    }
}
