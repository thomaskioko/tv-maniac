plugins {
    alias(libs.plugins.app.kmp)
    alias(libs.plugins.app.buildconfig)
}

scaffold {
    addAndroidTarget()
    useMetro()
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.core.appconfig.api)
            api(projects.api.tmdb.api)
            api(projects.api.trakt.api)
            implementation(projects.core.base)
        }
    }
}

buildConfig {
    packageName.set("com.thomaskioko.tvmaniac.appconfig")

    booleanField("IS_DEBUG", project.findProperty("app.debugOnly")?.toString()?.toBoolean() ?: false)

    stringField("TMDB_BASE_URL", "https://api.themoviedb.org/3")
    stringField("TRAKT_BASE_URL", "https://api.trakt.tv")

    buildConfigField("TMDB_API_KEY")
    buildConfigField("TRAKT_CLIENT_ID")
    buildConfigField("TRAKT_CLIENT_SECRET")
    buildConfigField("TRAKT_REDIRECT_URI")
    buildConfigField("SIMKL_CLIENT_ID")
    buildConfigField("SIMKL_CLIENT_SECRET")
    buildConfigField("SIMKL_REDIRECT_URI")
}
