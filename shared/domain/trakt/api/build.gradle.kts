import util.libs

plugins {
    `kmm-domain-plugin`
    kotlin("plugin.serialization") version ("1.6.10")

}

android {
    namespace = "com.thomaskioko.tvmaniac.trackt.auth.api"
}

dependencies {
    commonMainImplementation(project(":shared:core:util"))
    commonMainImplementation(project(":shared:core:database"))
    commonMainImplementation(libs.ktor.serialization)


    androidMainImplementation(project(":shared:core:util"))
    androidMainImplementation(libs.inject)
    androidMainImplementation(libs.appauth)
    androidMainImplementation(libs.androidx.activity)
    androidMainImplementation(libs.androidx.core)

}
