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
                api(projects.core.appconfig.api)
                api(projects.core.base)
                api(projects.core.util.api)
                api(projects.data.backup.api)
                api(projects.data.database.sqldelight)
                api(projects.data.datastore.api)

                implementation(libs.kotlinx.serialization.json)
            }
        }

        commonTest {
            dependencies {
                implementation(libs.bundles.unittest)
                implementation(projects.core.util.testing)
                implementation(projects.data.followedshows.api)
                implementation(projects.data.backup.testing)
                implementation(projects.data.database.testing)
                implementation(projects.data.datastore.testing)
            }
        }
    }
}
