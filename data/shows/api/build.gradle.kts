plugins {
    alias(libs.plugins.app.kmp)
}

scaffold {
    explicitApi()
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.data.database.sqldelight)

                api(libs.coroutines.core)
            }
        }

        commonTest {
            dependencies {
                implementation(projects.data.database.testing)

                implementation(libs.kotest.assertions)
                implementation(libs.kotlin.test)
            }
        }
    }
}
