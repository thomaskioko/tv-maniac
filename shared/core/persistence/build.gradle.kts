import util.libs

plugins {
    `kmm-domain-plugin`
}

android {
    namespace = "com.thomaskioko.tvmaniac.shared.domain.persistence"
}

dependencies {
    androidMainImplementation(libs.androidx.datastore)

    commonMainImplementation(project(":shared:core:ui"))
    commonMainImplementation(libs.kotlin.coroutines.core)
}
