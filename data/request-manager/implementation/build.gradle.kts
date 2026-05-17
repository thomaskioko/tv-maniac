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
                api(projects.core.util.api)
                api(projects.data.database.sqldelight)
                api(projects.data.requestManager.api)
            }
        }

        commonTest {
            dependencies {
                implementation(projects.core.util.testing)
                implementation(projects.data.database.testing)

                implementation(libs.kotlin.test)
                implementation(libs.kotest.assertions)
            }
        }
    }
}
