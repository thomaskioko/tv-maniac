plugins {
    alias(libs.plugins.app.kmp)
    alias(libs.plugins.app.buildconfig)
}

buildConfig {
    packageName.set("com.thomaskioko.tvmaniac.buildconfig.api")

    booleanField("IS_DEBUG", project.findProperty("app.debugOnly")?.toString()?.toBoolean() ?: false)

    // API base URLs
    stringField("TMDB_BASE_URL", "https://api.themoviedb.org/3")
    stringField("TRAKT_BASE_URL", "https://api.trakt.tv")

    buildConfigField("TMDB_API_KEY")
    buildConfigField("TRAKT_CLIENT_ID")
    buildConfigField("TRAKT_CLIENT_SECRET")
    buildConfigField("TRAKT_REDIRECT_URI")
}

scaffold {
    addAndroidMultiplatformTarget(withJava = true)
    explicitApi()
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(libs.coroutines.core)
        }
    }
}
