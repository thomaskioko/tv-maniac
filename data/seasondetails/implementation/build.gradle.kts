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
                implementation(projects.api.tmdb.api)
                implementation(projects.core.base)
                implementation(projects.core.networkUtil)
                implementation(projects.data.database.sqldelight)
                implementation(projects.data.datastore.api)
                implementation(projects.core.util)
                implementation(projects.core.util.api)
                implementation(projects.data.cast.api)
                implementation(projects.data.episode.api)
                implementation(projects.data.requestManager.api)
                implementation(projects.data.seasondetails.api)
                implementation(projects.data.seasons.api)

                implementation(libs.kotlinx.atomicfu)
                implementation(libs.sqldelight.extensions)
                implementation(libs.store5)
            }
        }
    }
}
