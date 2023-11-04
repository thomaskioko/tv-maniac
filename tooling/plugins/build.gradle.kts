plugins {
    `kotlin-dsl`
}

group = "com.thomaskioko.tvmaniac.plugins"

java {
    sourceCompatibility = JavaVersion.VERSION_18
    targetCompatibility = JavaVersion.VERSION_18

    toolchain {
        languageVersion.set(JavaLanguageVersion.of(18))
    }
}

dependencies {
    compileOnly(libs.android.gradle.tools)
    compileOnly(libs.kotlin.gradle)
}

gradlePlugin {
    plugins {
        register("kotlinMultiplatformPlugin") {
            id = "plugin.tvmaniac.multiplatform"
            implementationClass = "com.thomaskioko.tvmaniac.plugins.KotlinMultiplatformConventionPlugin"
        }
        register("androidApplication") {
            id = "tvmaniac.application"
            implementationClass = "com.thomaskioko.tvmaniac.plugins.ApplicationPlugin"
        }
        register("androidLibrary") {
            id = "plugin.tvmaniac.android.library"
            implementationClass = "com.thomaskioko.tvmaniac.plugins.AndroidLibraryPlugin"
        }
        register("kotlinAndroid") {
            id = "plugin.tvmaniac.kotlin.android"
            implementationClass = "com.thomaskioko.tvmaniac.plugins.KotlinAndroidPlugin"
        }
        register("androidComposeLibrary") {
            id = "tvmaniac.compose.library"
            implementationClass = "com.thomaskioko.tvmaniac.plugins.ComposeLibraryPlugin"
        }
        register("androidFeature") {
            id = "tvmaniac.android.feature"
            implementationClass = "com.thomaskioko.tvmaniac.plugins.FeaturePlugin"
        }
    }
}
