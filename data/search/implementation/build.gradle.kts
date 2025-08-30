plugins {
    alias(libs.plugins.tvmaniac.kmp)
}

tvmaniac {
    useDependencyInjection()
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(libs.coroutines.core)

                implementation(projects.api.tmdb.api)
                implementation(projects.core.base)
                implementation(projects.core.networkUtil)
                implementation(projects.core.util)
                implementation(projects.data.database.sqldelight)
                implementation(projects.data.search.api)

                implementation(libs.sqldelight.extensions)
                implementation(libs.store5)
            }
        }
    }
}
