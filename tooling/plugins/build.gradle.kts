plugins {
    `kotlin-dsl`
}

group = "com.thomaskioko.tvmaniac.plugins"

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

dependencies {
    compileOnly(libs.android.gradle.tools)
    compileOnly(libs.kotlin.gradle)
}

gradlePlugin {
    plugins {
        register("androidApplication") {
            id = "tvmaniac.application"
            implementationClass = "ApplicationPlugin"
        }
        register("androidLibrary") {
            id = "tvmaniac.android.library"
            implementationClass = "AndroidLibraryPlugin"
        }
        register("androidHilt") {
            id = "tvmaniac.hilt"
            implementationClass = "HiltPlugin"
        }
        register("androidComposeLibrary") {
            id = "tvmaniac.compose.library"
            implementationClass = "ComposeLibraryPlugin"
        }
        register("androidFeature") {
            id = "tvmaniac.android.feature"
            implementationClass = "FeaturePlugin"
        }
    }
}
