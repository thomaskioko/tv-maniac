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
        register("androidComposeLibrary") {
            id = "tvmaniac.compose.library"
            implementationClass = "ComposeLibraryPlugin"
        }
        register("androidFeature") {
            id = "tvmaniac.android.feature"
            implementationClass = "FeaturePlugin"
        }
        register("kmmDomain") {
            id = "tvmaniac.kmm.domain"
            implementationClass = "KotlinMultiplatformDomainPlugin"
        }
        register("kmmLibrary") {
            id = "tvmaniac.kmm.library"
            implementationClass = "KotlinMultiplatformLibraryPlugin"
        }
    }
}
