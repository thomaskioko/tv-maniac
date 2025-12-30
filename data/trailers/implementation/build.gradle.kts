plugins {
    alias(libs.plugins.app.kmp)
}

scaffold {
    useKotlinInject()
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.api.tmdb.api)
                implementation(projects.core.base)
                implementation(projects.core.util.api)
                implementation(projects.core.networkUtil)
                implementation(projects.data.requestManager.api)
                implementation(projects.data.shows.api)
                implementation(projects.data.trailers.api)

                implementation(libs.sqldelight.extensions)
                implementation(libs.kotlinx.atomicfu)
                implementation(libs.store5)
            }
        }
    }
}
