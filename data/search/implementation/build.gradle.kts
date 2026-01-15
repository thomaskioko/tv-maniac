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
                api(libs.coroutines.core)

                implementation(projects.api.tmdb.api)
                implementation(projects.api.trakt.api)
                implementation(projects.core.base)
                implementation(projects.core.networkUtil)
                implementation(projects.core.util.api)
                implementation(projects.data.database.sqldelight)
                implementation(projects.data.search.api)

                implementation(libs.sqldelight.extensions)
                implementation(libs.store5)
            }
        }
    }
}
