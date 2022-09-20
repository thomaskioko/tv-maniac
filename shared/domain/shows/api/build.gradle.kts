import util.libs

plugins {
    `kmm-domain-plugin`
}

android {
    namespace = "com.thomaskioko.tvmaniac.shared.domain.show_common.api"
}

dependencies {
    commonMainApi(project(":shared:core:util"))
    commonMainApi(project(":shared:core:database"))

    commonMainImplementation(libs.coroutines.core)
    commonMainImplementation(libs.multiplatform.paging.core)

}
