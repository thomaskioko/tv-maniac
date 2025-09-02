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
                implementation(projects.core.networkUtil)
                implementation(projects.data.database.sqldelight)
                implementation(projects.data.datastore.api)
                implementation(projects.core.util)
                implementation(projects.data.cast.api)
                implementation(projects.data.episodes.api)
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
