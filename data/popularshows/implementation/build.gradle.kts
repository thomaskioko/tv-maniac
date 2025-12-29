plugins {
    alias(libs.plugins.app.kmp)
}

scaffold {
    explicitApi()
    useKotlinInject()

    optIn(
        "androidx.paging.ExperimentalPagingApi",
    )
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.api.tmdb.api)
                implementation(projects.api.trakt.api)
                implementation(projects.core.base)
                implementation(projects.core.logger.api)
                implementation(projects.core.paging)
                implementation(projects.core.networkUtil)
                implementation(projects.core.util.api)
                implementation(projects.data.popularshows.api)
                implementation(projects.data.requestManager.api)
                implementation(projects.data.database.sqldelight)
                implementation(projects.data.shows.api)

                api(libs.coroutines.core)

                implementation(libs.sqldelight.extensions)
                implementation(libs.kotlinx.atomicfu)
                implementation(libs.store5)
            }
        }

        commonTest {
            dependencies {
                implementation(libs.bundles.unittest)
                implementation(projects.data.database.testing)
            }
        }
    }
}
