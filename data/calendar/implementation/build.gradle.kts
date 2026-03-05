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

                implementation(projects.api.trakt.api)
                implementation(projects.data.traktauth.api)
                implementation(projects.core.base)
                implementation(projects.core.networkUtil.api)
                implementation(projects.core.util.api)
                implementation(projects.data.calendar.api)
                implementation(projects.data.database.sqldelight)
                implementation(projects.data.requestManager.api)
                implementation(projects.data.shows.api)

                implementation(libs.sqldelight.extensions)
                implementation(libs.store5)
            }
        }
    }
}
