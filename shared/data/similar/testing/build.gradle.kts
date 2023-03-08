import org.jetbrains.kotlin.js.translate.context.Namer.kotlin

plugins {
    id("tvmaniac.kmm.library")
}

kotlin {
    android()
    ios()

    sourceSets {
        sourceSets["commonMain"].dependencies {
            implementation(project(":shared:core:util"))
            implementation(project(":shared:data:database"))
            implementation(project(":shared:data:similar:api"))
            implementation(libs.coroutines.core)
        }

    }
}

android {
    namespace = "com.thomaskioko.tvmaniac.similarshows.testing"
}