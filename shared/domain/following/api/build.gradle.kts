import org.jetbrains.kotlin.js.translate.context.Namer.kotlin

plugins {
    id("tvmaniac.kmm.api")
}

kotlin {
    android()
    ios()

    sourceSets {

        sourceSets["commonMain"].dependencies {
            api(libs.flowredux)

            implementation(project(":shared:domain:trakt:api"))
            implementation(libs.koin)
        }

        sourceSets["commonTest"].dependencies {
            implementation(kotlin("test"))
            implementation(project(":shared:domain:trakt:testing"))

            implementation(libs.testing.coroutines.test)
            implementation(libs.testing.kotest.assertions)
            implementation(libs.testing.turbine)
        }

    }
}
android {
    namespace = "com.thomaskioko.tvmaniac.domain.following.api"
}
