plugins {
    alias(libs.plugins.app.kmp)
}

scaffold {
    addAndroidMultiplatformTarget()
    useSerialization()
    useKotlinInject()
}

kotlin {
    sourceSets {
        androidMain { dependencies { implementation(libs.ktor.okhttp) } }

        commonMain {
            dependencies {
                implementation(projects.core.base)
                implementation(projects.core.logger.api)
                implementation(projects.api.tmdb.api)

                implementation(libs.ktor.core)
                implementation(libs.ktor.logging)
                implementation(libs.ktor.negotiation)
                implementation(libs.ktor.serialization.json)
                implementation(libs.sqldelight.extensions)
                implementation(libs.sqldelight.extensions)
            }
        }

        commonTest { dependencies { implementation(libs.ktor.serialization) } }

        iosMain {
            dependencies {
                implementation(libs.ktor.darwin)
                implementation(libs.ktor.negotiation)
            }
        }
    }
}
