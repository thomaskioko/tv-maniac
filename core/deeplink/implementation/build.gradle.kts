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
                api(projects.core.deeplink.api)
            }
        }

        commonTest {
            dependencies {
                implementation(libs.bundles.unittest)
            }
        }
    }
}
