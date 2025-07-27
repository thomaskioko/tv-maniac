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
                api(projects.data.database.sqldelight)
                api(projects.data.shows.api)
                implementation(projects.core.networkUtil)

                implementation(projects.core.base)

                api(libs.androidx.paging.common)
                api(libs.coroutines.core)
            }
        }
    }
}
