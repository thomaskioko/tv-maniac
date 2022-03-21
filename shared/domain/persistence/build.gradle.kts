import util.libs

plugins {
    `kmm-domain-plugin`
}

android {
    namespace = "com.thomaskioko.tvmaniac.shared.persistence"
}

dependencies {
    androidMainImplementation(libs.androidx.datastore)

    commonMainImplementation(projects.shared.core.ui)
    commonMainImplementation(libs.kotlin.coroutines.core)
}
