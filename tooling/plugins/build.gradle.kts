plugins {
    `kotlin-dsl`
}

group = "com.thomaskioko.tvmaniac.plugins"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17

    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.compose.compiler.gradlePlugin)
}

gradlePlugin {
    plugins {
        register("kotlinMultiplatformPlugin") {
            id = "plugin.tvmaniac.multiplatform"
            implementationClass = "com.thomaskioko.tvmaniac.plugins.KotlinMultiplatformConventionPlugin"
        }
        register("androidApplication") {
            id = "plugin.tvmaniac.application"
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
            id = "plugin.tvmaniac.compose.library"
            implementationClass = "com.thomaskioko.tvmaniac.plugins.ComposeLibraryPlugin"
        }
    }
}
