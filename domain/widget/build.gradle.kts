plugins {
    alias(libs.plugins.app.kmp)
}

scaffold {
    useMetro()
    useSerialization()
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(libs.coroutines.core)
                api(projects.core.base)
                api(projects.core.logger.api)
                api(projects.core.tasks.api)
                api(projects.data.upnext.api)

                implementation(libs.kotlinx.serialization.json)
                implementation(libs.okio)
            }
        }

        commonTest {
            dependencies {
                implementation(libs.bundles.unittest)
                implementation(projects.core.logger.testing)
                implementation(projects.data.upnext.testing)
            }
        }
    }
}
