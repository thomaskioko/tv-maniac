plugins {
    alias(libs.plugins.tvmaniac.kmp)
}

tvmaniac {
    multiplatform {
        useKotlinInjectAnvilCompiler()
    }

    optIn("kotlinx.coroutines.ExperimentalCoroutinesApi")
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(libs.coroutines.core)

                implementation(projects.api.tmdb.api)
                implementation(projects.core.base)
                implementation(projects.core.networkUtil)
                implementation(projects.data.database.sqldelight)
                implementation(projects.core.util)
                implementation(projects.data.watchlist.api)

                implementation(libs.sqldelight.extensions)
                implementation(libs.store5)
            }
        }
    }
}
