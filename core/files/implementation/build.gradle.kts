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
                api(projects.core.files.api)

                implementation(libs.kotlinx.serialization.json)
            }
        }

        commonTest {
            dependencies {
                implementation(libs.bundles.unittest)
                implementation(projects.core.files.testing)
            }
        }
    }
}
