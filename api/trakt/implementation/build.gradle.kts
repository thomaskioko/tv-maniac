plugins {
    alias(libs.plugins.app.kmp)
}

scaffold {
    addAndroidMultiplatformTarget()
    useKotlinInject()
    useSerialization()
}

kotlin {
    sourceSets {
        androidMain {
            dependencies {
                implementation(libs.appauth)
                implementation(libs.ktor.okhttp)
            }
        }

        commonMain {
            dependencies {
                implementation(projects.api.trakt.api)
                implementation(projects.core.base)
                implementation(projects.core.buildconfig.api)
                implementation(projects.core.logger.api)
                implementation(projects.data.datastore.api)
                implementation(projects.data.traktauth.api)

                implementation(libs.ktor.auth)
                implementation(libs.ktor.core)
                implementation(libs.ktor.logging)
                implementation(libs.ktor.negotiation)
                implementation(libs.ktor.serialization.json)
                implementation(libs.sqldelight.extensions)
            }
        }

        iosMain {
            dependencies {
                implementation(projects.api.trakt.api)

                implementation(libs.ktor.darwin)
            }
        }
    }
}
