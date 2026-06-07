plugins {
    alias(libs.plugins.app.kmp)
}

scaffold {
    addAndroidTarget()
    useMetro()
    useSerialization()
}

kotlin {
    sourceSets {
        androidMain {
            dependencies {
                implementation(libs.ktor.okhttp)
            }
        }

        commonMain {
            dependencies {
                api(libs.ktor.core)
                api(projects.api.trakt.api)
                api(projects.core.appconfig.api)
                api(projects.core.connectivity.api)
                api(projects.core.logger.api)
                api(projects.core.networkUtil.api)
                api(projects.data.connectedAccount.api)
                api(projects.data.episode.api)
                api(projects.data.followedshows.api)
                api(projects.data.library.api)
                api(projects.data.syncActivity.api)
                api(projects.data.traktauth.api)
                implementation(projects.core.base)
                implementation(libs.ktor.auth)
                implementation(libs.ktor.logging)
                implementation(libs.ktor.negotiation)
                implementation(libs.ktor.serialization.json)
            }
        }

        commonTest {
            dependencies {
                implementation(libs.bundles.unittest)
                implementation(libs.ktor.mock)
                implementation(libs.ktor.negotiation)
                implementation(libs.ktor.serialization.json)
                implementation(projects.api.trakt.api)
                implementation(projects.api.trakt.testing)
                implementation(projects.core.networkUtil.api)
            }
        }

        iosMain {
            dependencies {
                implementation(libs.ktor.darwin)
            }
        }
    }
}
