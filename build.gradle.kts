plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.android.test) apply false
    alias(libs.plugins.androidx.baselineprofile) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.dependency.analysis)
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.multiplatform) apply false
    alias(libs.plugins.android.kotlin.multiplatform) apply false
    alias(libs.plugins.moko.resources) apply false
    alias(libs.plugins.serialization) apply false
    alias(libs.plugins.skie) apply false
    alias(libs.plugins.spotless) apply false
    alias(libs.plugins.sqldelight) apply false
    alias(libs.plugins.roborazzi) apply false
    alias(libs.plugins.app.root) apply false
    alias(libs.plugins.app.android) apply false
    alias(libs.plugins.app.application) apply false
    alias(libs.plugins.app.baseline.profile) apply false
    alias(libs.plugins.app.kmp) apply false
    alias(libs.plugins.app.resource.generator) apply false
    alias(libs.plugins.app.spotless) apply false
}
