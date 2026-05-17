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
                implementation(libs.androidx.core.ktx)
            }
        }

        commonMain {
            dependencies {
                api(projects.core.notifications.api)
                api(projects.core.logger.api)
                api(projects.core.util.api)
                implementation(projects.core.base)
                implementation(libs.kotlinx.serialization.json)
            }
        }
    }
}
