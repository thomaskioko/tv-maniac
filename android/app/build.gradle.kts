import util.libs

plugins {
    `android-app-plugin`
}

android {
    namespace = "com.thomaskioko.tvmaniac"

    defaultConfig {
        buildConfigField("String", "TRAKT_CLIENT_ID", "\"" + propOrDef("TRAKT_CLIENT_ID", "") + "\"")
        buildConfigField("String", "TRAKT_CLIENT_SECRET", "\"" + propOrDef("TRAKT_CLIENT_SECRET", "") + "\"")
        buildConfigField("String", "TRAKT_REDIRECT_URI", "\"" + propOrDef("TRAKT_REDIRECT_URI", "") + "\"")
        buildConfigField("String", "TMDB_API_KEY", "\"" + propOrDef("TMDB_API_KEY", "") + "\"")
    }
}

dependencies {
    implementation(project(":shared:shared"))

    implementation(projects.android.core.compose)
    implementation(projects.android.core.navigation)
    implementation(projects.android.core.workmanager)
    implementation(projects.android.core.traktAuth)
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

    implementation(libs.appauth)
    implementation(libs.hilt.work)
    implementation(libs.androidx.compose.activity)
    implementation(libs.accompanist.systemuicontroller)
}

fun <T : Any> propOrDef(propertyName: String, defaultValue: T): T {
    @Suppress("UNCHECKED_CAST")
    val propertyValue = project.properties[propertyName] as T?
    return propertyValue ?: defaultValue
}
