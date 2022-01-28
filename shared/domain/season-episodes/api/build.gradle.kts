plugins {
    `kmm-domain-plugin`
}

dependencies {
    commonMainApi(project(":shared:core"))
    commonMainApi(project(":shared:database"))
    commonMainApi(libs.kotlin.coroutines.core)
    commonMainApi(libs.kermit)
}
