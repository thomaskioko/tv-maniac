plugins {
    alias(libs.plugins.tvmaniac.kmp)
}

tvmaniac {
    useKotlinInjectAnvilCompiler()
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.api.tmdb.api)
                implementation(projects.core.base)
                implementation(projects.core.networkUtil)
                implementation(projects.data.database.sqldelight)
                implementation(projects.data.episodes.api)
                implementation(projects.data.requestManager.api)
                implementation(projects.data.seasons.api)

                implementation(libs.sqldelight.extensions)
                implementation(libs.kotlinx.atomicfu)
                implementation(libs.store5)
            }
        }
    }
}
