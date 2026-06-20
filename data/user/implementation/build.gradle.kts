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
                api(libs.store5)
                api(projects.core.base)
                api(projects.data.accountManager.api)
                api(projects.data.database.sqldelight)
                api(projects.data.requestManager.api)
                api(projects.data.user.api)
                api(projects.core.util.api)

                implementation(projects.core.networkUtil.api)
                implementation(libs.sqldelight.extensions)
            }
        }

        commonTest {
            dependencies {
                implementation(libs.bundles.unittest)
                implementation(projects.core.util.testing)
                implementation(projects.data.database.testing)
                implementation(projects.data.requestManager.testing)
            }
        }
    }
}
