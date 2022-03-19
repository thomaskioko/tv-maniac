import util.libs

plugins {
    `kmm-domain-plugin`
}

android {
    namespace = "com.thomaskioko.tvmaniac.shared.core.ui"
}

dependencies {

    commonMainImplementation(projects.shared.core.util)
    commonMainImplementation(libs.kotlin.coroutines.core)
    commonMainImplementation(libs.koin.core)

    iosMainImplementation(libs.kotlin.coroutines.core)
    iosMainImplementation(libs.koin.core)
}
