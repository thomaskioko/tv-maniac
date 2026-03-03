plugins {
    alias(libs.plugins.app.kmp)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.core.base)
                implementation(projects.core.logger.api)
                api(projects.data.calendar.api)

                implementation(libs.coroutines.core)
                implementation(libs.kotlinInject.runtime)
            }
        }

        commonTest {
            dependencies {
                implementation(libs.bundles.unittest)
                implementation(projects.data.calendar.testing)
            }
        }
    }
}
