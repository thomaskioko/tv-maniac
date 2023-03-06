plugins {
    id("tvmaniac.application")
    id("tvmaniac.hilt")
}

android {
    namespace = "com.thomaskioko.tvmaniac"

    defaultConfig {
        applicationId = "com.thomaskioko.tvmaniac"
        versionCode = 1
        versionName = "1.0"

        buildConfigField("String", "TRAKT_CLIENT_ID", "\"" + propOrDef("TRAKT_CLIENT_ID", "") + "\"")
        buildConfigField("String", "TRAKT_CLIENT_SECRET", "\"" + propOrDef("TRAKT_CLIENT_SECRET", "") + "\"")
        buildConfigField("String", "TRAKT_REDIRECT_URI", "\"" + propOrDef("TRAKT_REDIRECT_URI", "") + "\"")
        buildConfigField("String", "TMDB_API_KEY", "\"" + propOrDef("TMDB_API_KEY", "") + "\"")
    }
}

dependencies {

    implementation(projects.android.core.compose)
    implementation(projects.android.core.navigation)
    implementation(projects.android.features.discover)
    implementation(projects.android.features.home)
    implementation(projects.android.features.search)
    implementation(projects.android.features.showDetails)
    implementation(projects.android.features.showsGrid)
    implementation(projects.android.features.following)
    implementation(projects.android.features.settings)
    implementation(projects.android.features.seasonDetails)
    implementation(projects.android.features.videoPlayer)
    implementation(projects.android.features.profile)
    implementation(projects.android.core.workmanager)
    implementation(projects.shared.shared)
    implementation(projects.shared.data.database)

    implementation(libs.accompanist.systemuicontroller)
    implementation(libs.androidx.compose.activity)
    implementation(libs.appauth)
    implementation(libs.hilt.work)
}

fun <T : Any> propOrDef(propertyName: String, defaultValue: T): T {
    @Suppress("UNCHECKED_CAST")
    val propertyValue = project.properties[propertyName] as T?
    return propertyValue ?: defaultValue
}
