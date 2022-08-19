import util.libs

plugins {
    `kmm-domain-plugin`
}

android {
    namespace = "com.thomaskioko.tvmaniac.shared.domain.show_common.api"
}

dependencies {
    commonMainImplementation(project(":shared:core:util"))
    commonMainImplementation(project(":shared:core:database"))

    commonMainImplementation(libs.coroutines.core)
    commonMainImplementation(libs.multiplatform.paging.core)

}
