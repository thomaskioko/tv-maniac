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
                api(libs.coroutines.core)
                api(libs.kotlinx.datetime)
                api(projects.core.base)
                api(projects.core.util.api)
                api(projects.data.database.sqldelight)
                api(projects.data.episode.api)
                api(projects.data.ratings.api)
                api(projects.data.watchStatus.api)
            }
        }

        commonTest {
            dependencies {
                implementation(libs.bundles.unittest)
                implementation(projects.core.util.testing)
                implementation(projects.data.database.testing)
                implementation(projects.data.episode.implementation)
                implementation(projects.data.ratings.implementation)
                implementation(projects.data.watchStatus.implementation)
            }
        }
    }
}
