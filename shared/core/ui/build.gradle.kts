import util.libs

plugins {
    `kmm-domain-plugin`
}

android {
    namespace = "com.thomaskioko.tvmaniac.shared.core.ui"
}

dependencies {

    androidMainImplementation(project(":shared:core:util"))
    androidMainImplementation(libs.inject)
    androidMainImplementation(libs.koin.core)

    commonMainImplementation(libs.kotlin.coroutines.core)
    commonMainImplementation(libs.koin.core)

    iosMainImplementation(libs.koin.core)
}
