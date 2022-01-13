plugins {
    `kmm-domain-plugin`
}

dependencies {
    commonMainImplementation(project(":shared:core"))
    commonMainImplementation(project(":shared:database"))
    commonMainImplementation(project(":shared:domain:seasons:api"))
    commonMainImplementation(libs.kotlin.coroutines.core)
}
