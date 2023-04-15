plugins {
    id("tvmaniac.application")
    alias(libs.plugins.ksp)
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

    implementation(projects.android.core.designsystem)
    implementation(projects.android.core.navigation)
    implementation(projects.android.core.traktAuth)
    implementation(projects.android.core.workmanager)
    implementation(projects.android.features.discover)
    implementation(projects.android.features.home)
    implementation(projects.android.features.search)
    implementation(projects.android.features.showDetails)
    implementation(projects.android.features.showsGrid)
    implementation(projects.android.features.following)
    implementation(projects.android.features.settings)
    implementation(projects.android.features.seasonDetails)
    implementation(projects.android.features.trailers)
    implementation(projects.android.features.profile)

    implementation(projects.shared.shared)
    implementation(projects.shared.core.base)
    implementation(projects.shared.data.category.implementation)
    implementation(projects.shared.data.datastore.implementation)
    implementation(projects.shared.data.episodes.implementation)
    implementation(projects.shared.data.profile.implementation)
    implementation(projects.shared.data.seasonDetails.implementation)
    implementation(projects.shared.data.similar.implementation)
    implementation(projects.shared.data.shows.implementation)
    implementation(projects.shared.data.tmdb.implementation)
    implementation(projects.shared.data.trailers.implementation)
    implementation(projects.shared.data.traktApi.implementation)
    implementation(projects.shared.domain.seasondetails)
    implementation(projects.shared.domain.settings)
    implementation(projects.shared.domain.showDetails)

    implementation(libs.accompanist.systemuicontroller)
    implementation(libs.androidx.compose.activity)
    implementation(libs.appauth)

    implementation(libs.kotlinInject.runtime)
    ksp(libs.kotlinInject.compiler)
}

fun <T : Any> propOrDef(propertyName: String, defaultValue: T): T {
    @Suppress("UNCHECKED_CAST")
    val propertyValue = project.properties[propertyName] as T?
    return propertyValue ?: defaultValue
}
