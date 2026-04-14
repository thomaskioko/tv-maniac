plugins {
    alias(libs.plugins.app.kmp)
}

scaffold {
    useMetro()
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(projects.core.networkUtil.api)
                api(projects.data.shows.api)

                implementation(projects.data.database.sqldelight)
                implementation(projects.core.base)

                api(libs.androidx.paging.common)
                api(libs.coroutines.core)
            }
        }
    }
}
