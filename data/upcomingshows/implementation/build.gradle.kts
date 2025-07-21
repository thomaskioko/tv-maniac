plugins {
    alias(libs.plugins.tvmaniac.kmp)
}

tvmaniac {
    useKotlinInjectAnvilCompiler()

    optIn("androidx.paging.ExperimentalPagingApi", "kotlin.time.ExperimentalTime")
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
                implementation(projects.core.util)
                implementation(projects.data.upcomingshows.api)
                implementation(projects.data.requestManager.api)

                api(libs.coroutines.core)

                implementation(libs.kotlinx.atomicfu)
                implementation(libs.sqldelight.extensions)
                implementation(libs.store5)
            }
        }
    }
}
