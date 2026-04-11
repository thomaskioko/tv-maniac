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
                api(projects.data.database.sqldelight)
                api(projects.data.shows.api)

                implementation(projects.core.base)

                api(libs.coroutines.core)
            }
        }
    }
}
