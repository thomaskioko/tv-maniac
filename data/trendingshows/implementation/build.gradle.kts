plugins {
    alias(libs.plugins.tvmaniac.kmp)
}

tvmaniac {
    useDependencyInjection()

    optIn(
        "androidx.paging.ExperimentalPagingApi",
    )
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.api.tmdb.api)
                implementation(projects.core.base)
                implementation(projects.core.logger.api)
                implementation(projects.core.paging)
                implementation(projects.data.database.sqldelight)
                implementation(projects.data.trendingshows.api)
                implementation(projects.core.util)
                implementation(projects.data.requestManager.api)

                api(libs.coroutines.core)

                implementation(libs.sqldelight.extensions)
                implementation(libs.kotlinx.atomicfu)
                implementation(libs.store5)
            }
        }
    }
}
