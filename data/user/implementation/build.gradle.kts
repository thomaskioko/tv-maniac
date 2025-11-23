plugins {
    alias(libs.plugins.app.kmp)
}

scaffold {
    explicitApi()
    useKotlinInject()
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.api.trakt.api)
                implementation(projects.core.base)
                implementation(projects.core.networkUtil)
                implementation(projects.core.util)
                implementation(projects.data.database.sqldelight)
                implementation(projects.data.requestManager.api)
                implementation(projects.data.user.api)
                implementation(projects.data.traktauth.api)

                api(libs.coroutines.core)

                implementation(libs.sqldelight.extensions)
                implementation(libs.store5)
            }
        }

        commonTest {
            dependencies {
                implementation(libs.bundles.unittest)
                implementation(projects.data.database.testing)
                implementation(projects.core.util.testing)
            }
        }
    }
}
