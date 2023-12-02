plugins {
    id("plugin.tvmaniac.kotlin.android")
    id("plugin.tvmaniac.multiplatform")
    alias(libs.plugins.serialization)
}

kotlin {
    sourceSets {
        androidMain.dependencies {
            implementation(projects.androidCore.designsystem)
            implementation(projects.androidCore.resources)

            implementation(projects.feature.discover)
            implementation(projects.feature.library)
            implementation(projects.feature.moreShows)
            implementation(projects.feature.search)
            implementation(projects.feature.seasonDetails)
            implementation(projects.feature.settings)
            implementation(projects.feature.showDetails)
            implementation(projects.feature.trailers)

            implementation(libs.androidx.compose.material.icons)
            implementation(libs.androidx.compose.material3)
            implementation(libs.decompose.extensions.compose)
        }

        commonMain.dependencies {
            implementation(projects.core.traktAuth.api)
            implementation(projects.core.util)

            api(projects.presentation.discover)
            api(projects.presentation.library)
            api(projects.presentation.moreShows)
            api(projects.presentation.profile)
            api(projects.presentation.search)
            api(projects.presentation.seasondetails)
            api(projects.presentation.settings)
            api(projects.presentation.showDetails)
            api(projects.presentation.trailers)

            implementation(libs.kotlinInject.runtime)
        }
    }
}

android {
    namespace = "com.thomaskioko.tvmaniac.navigation"

    buildFeatures {
        compose = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_18
        targetCompatibility = JavaVersion.VERSION_18
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.composecompiler.get()
    }
}
