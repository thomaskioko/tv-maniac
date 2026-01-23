plugins {
    alias(libs.plugins.app.kmp)
}

scaffold {
    useKotlinInject()

    optIn("kotlinx.coroutines.ExperimentalCoroutinesApi")
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(libs.coroutines.core)

                implementation(projects.api.trakt.api)
                implementation(projects.core.base)
                implementation(projects.core.logger.api)
                implementation(projects.core.networkUtil.api)
                implementation(projects.core.util.api)
                implementation(projects.data.database.sqldelight)
                implementation(projects.data.followedshows.api)
                implementation(projects.data.requestManager.api)
                implementation(projects.data.traktauth.api)

                implementation(libs.kotlinx.datetime)
                implementation(libs.sqldelight.extensions)
            }
        }

        commonTest {
            dependencies {
                implementation(libs.bundles.unittest)
                implementation(projects.core.logger.testing)
                implementation(projects.core.util.testing)
                implementation(projects.data.database.sqldelight)
                implementation(projects.data.database.testing)
                implementation(projects.data.requestManager.testing)
                implementation(projects.data.traktauth.testing)
            }
        }
    }
}
