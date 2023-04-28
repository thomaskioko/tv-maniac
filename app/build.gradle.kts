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

    implementation(projects.androidCore.designsystem)
    implementation(projects.androidCore.navigation)
    implementation(projects.androidCore.traktAuth)
    implementation(projects.androidCore.workmanager)
    implementation(projects.androidFeatures.discover)
    implementation(projects.androidFeatures.home)
    implementation(projects.androidFeatures.search)
    implementation(projects.androidFeatures.showDetails)
    implementation(projects.androidFeatures.showsGrid)
    implementation(projects.androidFeatures.following)
    implementation(projects.androidFeatures.settings)
    implementation(projects.androidFeatures.seasonDetails)
    implementation(projects.androidFeatures.trailers)
    implementation(projects.androidFeatures.profile)

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