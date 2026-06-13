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
                api(libs.coroutines.core)
                api(projects.api.tmdb.api)
                api(projects.core.base)
                api(projects.core.logger.api)
                api(projects.data.accountManager.api)
                api(projects.data.database.sqldelight)
                api(projects.data.shows.api)

                implementation(libs.sqldelight.extensions)
            }
        }

        androidMain {
            dependencies {
                implementation(projects.core.networkUtil.api)
            }
        }

        commonTest {
            dependencies {
                implementation(libs.bundles.unittest)
                implementation(projects.api.tmdb.testing)
                implementation(projects.core.logger.testing)
                implementation(projects.data.database.testing)
            }
        }
    }
}
