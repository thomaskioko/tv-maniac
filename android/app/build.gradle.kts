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

    implementation(project(":shared:util"))
    implementation(project(":shared:networkutil"))
    implementation(project(":shared:data:category:api"))
    implementation(project(":shared:data:category:implementation"))
    implementation(project(":shared:data:database"))
    implementation(project(":shared:data:datastore:api"))
    implementation(project(":shared:data:datastore:implementation"))
    implementation(project(":shared:data:episodes:api"))
    implementation(project(":shared:data:episodes:implementation"))
    implementation(project(":shared:data:profile:api"))
    implementation(project(":shared:data:profile:implementation"))
    implementation(project(":shared:data:similar:api"))
    implementation(project(":shared:data:similar:implementation"))
    implementation(project(":shared:data:season-details:api"))
    implementation(project(":shared:data:season-details:implementation"))
    implementation(project(":shared:data:shows:api"))
    implementation(project(":shared:data:shows:implementation"))
    implementation(project(":shared:data:trailers:api"))
    implementation(project(":shared:data:trailers:implementation"))
    implementation(project(":shared:data:tmdb:api"))
    implementation(project(":shared:data:tmdb:implementation"))
    implementation(project(":shared:data:trakt-api:api"))
    implementation(project(":shared:data:trakt-api:implementation"))
    implementation(project(":shared:domain:discover"))
    implementation(project(":shared:domain:following"))
    implementation(project(":shared:domain:seasondetails"))
    implementation(project(":shared:domain:settings"))
    implementation(project(":shared:domain:show-details"))
    implementation(project(":shared:domain:trailers"))

    implementation(libs.accompanist.systemuicontroller)
    implementation(libs.androidx.compose.activity)
    implementation(libs.appauth)

    implementation(libs.kotlinInject.runtime)
    ksp(libs.kotlinInject.compiler)
}