plugins {
    id(Plugins.androidApplication)
    kotlin(Plugins.android)
    kotlin(Plugins.kapt)
    kotlin(Plugins.serialization) version("1.5.10")
}

android {
    compileSdk = libs.versions.android.compile.get().toInt()
    defaultConfig {
        applicationId = "com.thomaskioko.tvmaniac.android"
        minSdk = libs.versions.android.min.get().toInt()
        targetSdk = libs.versions.android.target.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }

    buildFeatures {
        compose = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.androidx.compose.get()
    }

    dependencies {
        implementation(project(":shared"))

        implementation(libs.material)
        implementation(libs.androidx.appCompat)

        implementation(libs.androidx.compose.runtime.core)
        implementation(libs.androidx.compose.runtime.livedata)
        implementation(libs.androidx.compose.material)
        implementation(libs.androidx.compose.ui.runtime)
        implementation(libs.androidx.compose.ui.tooling)
        implementation(libs.androidx.compose.foundation)
        implementation(libs.androidx.compose.compiler)
        implementation(libs.androidx.compose.constraintlayout)
        implementation(libs.androidx.compose.activity)
        implementation(libs.androidx.compose.navigation)

        implementation(libs.kotlin.datetime)

        implementation(libs.ktor.android)

        debugImplementation(libs.squareup.leakcanary)
    }
}