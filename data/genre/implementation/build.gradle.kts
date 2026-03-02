plugins {
    alias(libs.plugins.app.kmp)
}

scaffold {
    useKotlinInject()

    optIn(
        "kotlinx.coroutines.DelicateCoroutinesApi",
    )
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(libs.coroutines.core)

                implementation(projects.api.tmdb.api)
                implementation(projects.api.trakt.api)
                implementation(projects.core.base)
                implementation(projects.core.logger.api)
                implementation(projects.core.networkUtil.api)
                implementation(projects.core.util.api)
                implementation(projects.data.datastore.api)
                implementation(projects.data.database.sqldelight)
                implementation(projects.data.requestManager.api)
                implementation(projects.data.shows.api)
                implementation(projects.domain.genre)

                implementation(libs.kotlinx.datetime)
                implementation(libs.sqldelight.extensions)
                implementation(libs.store5)
            }
        }
    }
}
