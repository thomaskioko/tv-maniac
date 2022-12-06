import util.libs

plugins {
    `kmm-domain-plugin`
}

android {
    namespace = "com.thomaskioko.tvmaniac.shared.domain.settings.api"
}

dependencies {
    commonMainApi(project(":shared:core:ui"))

    commonMainImplementation(libs.coroutines.core)
}
