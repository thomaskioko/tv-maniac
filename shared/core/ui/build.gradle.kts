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
    androidMainImplementation(libs.koin)

    commonMainImplementation(libs.koin)
    commonMainImplementation(libs.coroutines.core)

    iosMainImplementation(libs.koin)
}
