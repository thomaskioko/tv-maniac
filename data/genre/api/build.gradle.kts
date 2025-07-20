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
                api(projects.data.database.sqldelight)
                api(projects.data.shows.api)

                implementation(projects.core.base)

                api(libs.coroutines.core)
            }
        }
    }
}
