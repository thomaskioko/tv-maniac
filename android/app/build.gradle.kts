import com.thomaskioko.tvmaniac.extensions.TvManiacBuildType

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
    }

    buildTypes {
        debug {
            applicationIdSuffix = TvManiacBuildType.DEBUG.applicationIdSuffix
        }
    }

    packaging {
        resources {
            excludes.add("/META-INF/{AL2.0,LGPL2.1}")
        }
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

    implementation(projects.shared.core.database)
    implementation(projects.shared.core.networkutil)
    implementation(projects.shared.core.util)
    implementation(projects.shared.core.datastore.api)
    implementation(projects.shared.core.datastore.implementation)
    implementation(projects.shared.core.tmdbApi.api)
    implementation(projects.shared.core.tmdbApi.implementation)
    implementation(projects.shared.core.traktApi.api)
    implementation(projects.shared.core.traktApi.implementation)

    implementation(projects.shared.data.category.api)
    implementation(projects.shared.data.category.implementation)
    implementation(projects.shared.data.episodes.api)
    implementation(projects.shared.data.episodes.implementation)
    implementation(projects.shared.data.profile.api)
    implementation(projects.shared.data.profile.implementation)
    implementation(projects.shared.data.similar.api)
    implementation(projects.shared.data.similar.implementation)
    implementation(projects.shared.data.seasonDetails.api)
    implementation(projects.shared.data.seasonDetails.implementation)
    implementation(projects.shared.data.shows.api)
    implementation(projects.shared.data.shows.implementation)
    implementation(projects.shared.data.trailers.api)
    implementation(projects.shared.data.trailers.implementation)

    implementation(projects.shared.domain.discover)
    implementation(projects.shared.domain.following)
    implementation(projects.shared.domain.seasondetails)
    implementation(projects.shared.domain.settings)
    implementation(projects.shared.domain.showDetails)
    implementation(projects.shared.domain.trailers)

    implementation(libs.accompanist.systemuicontroller)
    implementation(libs.androidx.compose.activity)
    implementation(libs.appauth)

    implementation(libs.kotlinInject.runtime)
    ksp(libs.kotlinInject.compiler)
}