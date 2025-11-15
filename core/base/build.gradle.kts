plugins {
    alias(libs.plugins.app.kmp)
    alias(libs.plugins.app.buildconfig)
}

buildConfig {
    packageName.set("com.thomaskioko.tvmaniac.core.base")

    booleanField("IS_DEBUG", project.findProperty("app.debugOnly")?.toString()?.toBoolean() ?: false)
    stringField("TMDB_BASE_URL", "https://api.themoviedb.org/3")
    stringField("TRAKT_BASE_URL", "https://api.trakt.tv")

    buildConfigField("TMDB_API_KEY")
    buildConfigField("TRAKT_CLIENT_ID")
    buildConfigField("TRAKT_CLIENT_SECRET")
    buildConfigField("TRAKT_REDIRECT_URI")
}

scaffold {
    addAndroidMultiplatformTarget(withJava = true)
    useKotlinInject()
    useSerialization()

    optIn(
        "kotlinx.coroutines.InternalCoroutinesApi",
        "kotlinx.coroutines.ExperimentalCoroutinesApi",
    )
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.core.view)

            implementation(libs.coroutines.core)
            implementation(libs.decompose.decompose)
        }
    }
}

/**
 * Workaround for Gradle 9.0 implicit dependency issue, The extractAndroidMainAnnotations task
 * uses output from kspAndroidMain,but doesn't declare an explicit dependency, causing build failures
 */
tasks.configureEach {
    if (name == "extractAndroidMainAnnotations") {
        dependsOn("kspAndroidMain")
    }
}
