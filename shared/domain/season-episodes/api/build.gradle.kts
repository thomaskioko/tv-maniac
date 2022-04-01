import util.libs

plugins {
    `kmm-domain-plugin`
}

android {
    namespace = "com.thomaskioko.tvmaniac.shared.domain.seasonepisodes.api"
}

dependencies {
    commonMainApi(projects.shared.core.util)
    commonMainImplementation(projects.shared.core.ui)
    commonMainImplementation(projects.shared.database)
    commonMainImplementation(projects.shared.domain.showCommon.api)

    commonMainImplementation(libs.kotlin.coroutines.core)
}
