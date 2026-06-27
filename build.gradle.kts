import com.autonomousapps.DependencyAnalysisExtension

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.android.test) apply false
    alias(libs.plugins.androidx.baselineprofile) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.firebase.crashlytics.gradle) apply false
    alias(libs.plugins.google.services) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.metro) apply false
    alias(libs.plugins.multiplatform) apply false
    alias(libs.plugins.android.kotlin.multiplatform) apply false
    alias(libs.plugins.moko.resources) apply false
    alias(libs.plugins.serialization) apply false
    alias(libs.plugins.spotless) apply false
    alias(libs.plugins.sqldelight) apply false
    alias(libs.plugins.roborazzi) apply false
    alias(libs.plugins.app.root)
    alias(libs.plugins.app.lint)
    alias(libs.plugins.app.android) apply false
    alias(libs.plugins.app.application) apply false
    alias(libs.plugins.app.baseline.profile) apply false
    alias(libs.plugins.app.kmp) apply false
    alias(libs.plugins.app.resource.generator) apply false
    alias(libs.plugins.app.spotless) apply false
    alias(libs.plugins.app.buildconfig) apply false
}

// TEMPORARY: dependency-analysis 3.16.0 mis-reports KMP commonMain/commonTest project dependencies
// as unused (mirrored "declare in androidMain" advice that breaks compilation if applied, plus test
// fakes consumed via fixtures). Remove this block once the suppression ships in app-gradle-plugins.
configure<DependencyAnalysisExtension> {
    issues {
        all {
            sourceSet("commonMain") {
                onUnusedDependencies { severity("ignore") }
            }
            sourceSet("commonTest") {
                onUnusedDependencies { severity("ignore") }
            }
            sourceSet("jvmAndroidMain") {
                onUnusedDependencies { severity("ignore") }
            }
            sourceSet("androidMain") {
                onUsedTransitiveDependencies { severity("ignore") }
            }
            sourceSet("androidDeviceTest") {
                onUsedTransitiveDependencies { severity("ignore") }
            }
        }
    }
}
