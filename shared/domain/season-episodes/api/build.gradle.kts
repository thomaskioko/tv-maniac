import util.libs

plugins {
    `kmm-domain-plugin`
}

android {
    namespace = "com.thomaskioko.tvmaniac.seasonepisodes.api"
}

dependencies {
    commonMainApi(projects.shared.core.ui)
    commonMainApi(projects.shared.core.util)
    commonMainApi(projects.shared.database)
    commonMainApi(libs.kotlin.coroutines.core)
    commonMainApi(libs.kermit)

    commonMainImplementation(projects.shared.domain.showCommon.api)
}
