plugins {
    id(Plugins.androidApplication)
    kotlin(Plugins.android)
    kotlin(Plugins.kapt)
    kotlin(Plugins.serialization) version("1.5.10")
    id(Plugins.hilt)
}

android {
    compileSdk = libs.versions.android.compile.get().toInt()
    defaultConfig {
        applicationId = "com.thomaskioko.tvmaniac"
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

    tasks.withType<Test>().configureEach {
        useJUnitPlatform()
    }

    dependencies {
        implementation(project(":shared"))

        implementation(libs.accompanist.pager)

        implementation(libs.androidx.appCompat)

        implementation(libs.androidx.compose.runtime.core)
        implementation(libs.androidx.compose.runtime.livedata)
        implementation(libs.androidx.compose.material)
        implementation(libs.androidx.compose.ui.runtime)
        implementation(libs.androidx.compose.ui.tooling)
        implementation(libs.androidx.compose.ui.util)
        implementation(libs.androidx.compose.foundation)
        implementation(libs.androidx.compose.compiler)
        implementation(libs.androidx.compose.constraintlayout)
        implementation(libs.androidx.compose.activity)
        implementation(libs.androidx.compose.navigation)

        implementation(libs.androidx.lifecycle.common)
        implementation(libs.androidx.lifecycle.runtime)

        implementation(libs.hilt.android)
        implementation(libs.hilt.navigation)
        
        implementation(libs.kotlin.datetime)
        implementation(libs.ktor.android)
        kapt(libs.hilt.compiler)

        implementation(libs.coil)
        implementation(libs.material)
        implementation(libs.napier)
        debugImplementation(libs.squareup.leakcanary)

        testImplementation(libs.testing.turbine)
        testImplementation(libs.testing.coroutines.test)
        testImplementation(libs.testing.kotest.assertions)

        testImplementation(libs.testing.mockito.inline)
        testImplementation(libs.testing.mockk.core)

        testImplementation(libs.testing.androidx.core)

        testImplementation(libs.testing.junit5.api)
        testRuntimeOnly(libs.testing.junit5.jupiter)
        testRuntimeOnly(libs.testing.junit5.engine)
        testRuntimeOnly(libs.testing.junit5.vintage)

    }
}