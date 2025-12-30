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
                implementation(projects.data.database.sqldelight)
                implementation(projects.data.showdetails.api)
                implementation(projects.core.util.api)
                implementation(projects.core.util)
                implementation(projects.data.cast.api)
                implementation(projects.data.seasons.api)
                implementation(projects.data.trailers.api)
                implementation(projects.data.requestManager.api)

                api(libs.coroutines.core)

                implementation(libs.sqldelight.extensions)
                implementation(libs.kotlinx.atomicfu)
                implementation(libs.store5)
            }
        }
    }
}
