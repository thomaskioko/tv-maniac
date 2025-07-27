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
                api(projects.core.networkUtil)
                api(projects.data.shows.api)

                implementation(projects.data.database.sqldelight)
                implementation(projects.core.base)

                api(libs.androidx.paging.common)
                api(libs.coroutines.core)
            }
        }
    }
}
