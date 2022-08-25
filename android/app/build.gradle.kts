import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties
import util.libs

plugins {
    `android-app-plugin`
}

android {
    namespace = "com.thomaskioko.tvmaniac"

    buildTypes {
        getByName("debug") {
            val properties = gradleLocalProperties(rootDir)
            val traktClientId: String = properties.getProperty("TRAKT_CLIENT_ID")
            val traktClientSecret: String = properties.getProperty("TRAKT_CLIENT_SECRET")
            val traktRedirectUri: String = properties.getProperty("TRAKT_REDIRECT_URI")
            val tmdbApiKey: String = properties.getProperty("TMDB_API_KEY")

            buildConfigField("String", "TRAKT_CLIENT_ID", traktClientId)
            buildConfigField("String", "TRAKT_CLIENT_SECRET", traktClientSecret)
            buildConfigField("String", "TRAKT_REDIRECT_URI", traktRedirectUri)
            buildConfigField("String", "TMDB_API_KEY", tmdbApiKey)
        }
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
    implementation(projects.android.features.seasons)
    implementation(projects.android.features.videoPlayer)

    implementation(libs.appauth)
    implementation(libs.hilt.work)
    implementation(libs.androidx.compose.activity)
    implementation(libs.accompanist.systemuicontroller)
}
