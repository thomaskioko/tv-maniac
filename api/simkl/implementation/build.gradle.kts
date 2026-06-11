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
                api(projects.api.simkl.api)
                api(projects.core.appconfig.api)
                api(projects.core.logger.api)
                api(projects.core.networkUtil.api)
                api(projects.data.accountManager.api)
                api(projects.data.oauth.api)
                api(projects.data.user.api)
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
                implementation(libs.ktor.negotiation)
                implementation(libs.ktor.serialization.json)
                implementation(projects.api.simkl.api)
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
