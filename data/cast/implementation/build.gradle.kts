plugins {
    alias(libs.plugins.app.kmp)
}

scaffold {
    useKotlinInject()

    optIn(
        "kotlinx.coroutines.ExperimentalCoroutinesApi",
    )
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.api.tmdb.api)
                implementation(projects.api.trakt.api)
                implementation(projects.core.base)
                implementation(projects.core.networkUtil)
                implementation(projects.core.util.api)
                implementation(projects.data.database.sqldelight)
                implementation(projects.data.cast.api)
                implementation(projects.data.requestManager.api)
                implementation(projects.data.shows.api)

                implementation(libs.sqldelight.extensions)
                implementation(libs.kotlinx.atomicfu)
                implementation(libs.store5)
            }
        }

        commonTest {
            dependencies {
                implementation(projects.data.database.testing)

                implementation(libs.bundles.unittest)
            }
        }
    }
}
