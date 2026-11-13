buildscript {
    dependencies {
        constraints {
            classpath("com.autonomousapps:dependency-analysis-gradle-plugin") {
                version { strictly("3.17.0") }
                because("DAGP 3.18+ runs buildHealth out of memory, see autonomousapps/dependency-analysis-gradle-plugin#1878")
            }
            classpath("com.google.devtools.ksp:symbol-processing-gradle-plugin") {
                version { strictly("2.3.12") }
                because("KSP 2.3.13 sometimes writes no output for a module when KSP tasks run in parallel")
            }
        }
    }
}

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
    alias(libs.plugins.app.android) apply false
    alias(libs.plugins.app.application) apply false
    alias(libs.plugins.app.baseline.profile) apply false
    alias(libs.plugins.app.kmp) apply false
    alias(libs.plugins.app.resource.generator) apply false
    alias(libs.plugins.app.spotless)
    alias(libs.plugins.app.buildconfig) apply false
}
