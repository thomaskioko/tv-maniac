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

            implementation(projects.presentation.discover)
            implementation(projects.presentation.library)
            implementation(projects.presentation.moreShows)
            implementation(projects.presentation.search)
            implementation(projects.presentation.seasondetails)
            implementation(projects.presentation.settings)
            implementation(projects.presentation.showDetails)
            implementation(projects.presentation.trailers)

            implementation(libs.kotlinInject.runtime)

            api(libs.decompose.decompose)
            api(libs.essenty.lifecycle)
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
