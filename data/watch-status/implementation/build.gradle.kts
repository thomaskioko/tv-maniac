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
                api(projects.data.watchStatus.api)
                api(projects.data.database.sqldelight)
                api(projects.core.base)
                api(projects.core.util.api)
                api(libs.coroutines.core)

                implementation(libs.sqldelight.extensions)
            }
        }

        commonTest {
            dependencies {
                implementation(libs.bundles.unittest)
                implementation(projects.data.watchStatus.testing)
                implementation(projects.data.database.testing)
                implementation(projects.core.util.testing)
            }
        }
    }
}
