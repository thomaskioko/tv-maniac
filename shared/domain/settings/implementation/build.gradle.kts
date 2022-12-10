import org.jetbrains.kotlin.kapt3.base.Kapt.kapt
import util.libs

plugins {
    `kmm-domain-plugin`
    kotlin("kapt")
    id("dagger.hilt.android.plugin")
}

android {
    namespace = "com.thomaskioko.tvmaniac.shared.domain.settings"
}

dependencies {
    androidMainImplementation(libs.hilt.android)
    kapt(libs.hilt.compiler)

    commonMainImplementation(project(":shared:domain:settings:api"))
    commonMainImplementation(libs.coroutines.core)
    commonMainImplementation(libs.androidx.datastore.preference)
    commonMainImplementation(libs.koin)

    commonTestImplementation(kotlin("test"))
    commonTestImplementation(project(":shared:core:test"))

    commonTestImplementation(libs.testing.coroutines.test)
    commonTestImplementation(libs.testing.kotest.assertions)
    commonTestImplementation(libs.testing.turbine)

}
