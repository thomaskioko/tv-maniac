plugins {
    alias(libs.plugins.app.kmp)
}

scaffold {
    useMetro()
}

kotlin {
    sourceSets {
        androidMain {
            dependencies {
                implementation(libs.kotlinx.datetime)
            }
        }

        commonMain {
            dependencies {
                api(libs.coroutines.core)
                api(projects.core.base)
                api(projects.core.util.api)
                api(projects.data.datastore.api)
                api(projects.domain.theme)
            }
        }

        commonTest {
            dependencies {
                implementation(libs.bundles.unittest)
                implementation(projects.data.datastore.testing)
            }
        }
    }
}
