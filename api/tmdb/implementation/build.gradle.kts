plugins {
    alias(libs.plugins.app.kmp)
}

scaffold {
    addAndroidTarget()
    useSerialization()
    useMetro()
}

kotlin {
    sourceSets {
        androidMain { dependencies { implementation(libs.ktor.okhttp) } }

        commonMain {
            dependencies {
                api(libs.ktor.core)
                api(projects.api.tmdb.api)
                api(projects.core.appconfig.api)
                api(projects.core.connectivity.api)
                api(projects.core.logger.api)
                api(projects.core.networkUtil.api)
                implementation(projects.core.base)
                implementation(libs.ktor.logging)
                implementation(libs.ktor.negotiation)
                implementation(libs.ktor.serialization.json)
            }
        }

        commonTest { dependencies { implementation(libs.ktor.serialization) } }

        iosMain { dependencies { implementation(libs.ktor.darwin) } }
    }
}
