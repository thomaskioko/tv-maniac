plugins {
    `kmm-domain-plugin`
}

dependencies {
    commonMainApi(project(":shared:core"))
    commonMainApi(project(":shared:database"))
    commonMainApi(project(":shared:domain:show-common:api"))
    commonMainApi(libs.kotlin.coroutines.core)
}
