import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING

plugins {
    id("tvmaniac.kmm.library")
    alias(libs.plugins.serialization)
    alias(libs.plugins.ksp)
}

kotlin {
    android()
    ios()

    sourceSets {
        sourceSets["androidMain"].dependencies {
            implementation(project(":shared:core:util"))
            implementation(project(":shared:data:network"))
            implementation(libs.appauth)
            implementation(libs.ktor.okhttp)
        }


        sourceSets["commonMain"].dependencies {
            implementation(project(":shared:data:network"))
            implementation(project(":shared:data:trakt-api:api"))
            implementation(libs.sqldelight.extensions)
            implementation(libs.koin)
            implementation(libs.ktor.core)
            implementation(libs.ktor.negotiation)
            implementation(libs.ktor.logging)
            implementation(libs.ktor.serialization.json)
            implementation(libs.ktor.serialization)
            implementation(libs.kotlinInject.runtime)
        }

        sourceSets["iosMain"].dependencies {
            implementation(project(":shared:data:network"))
            implementation(project(":shared:data:trakt-api:api"))
            implementation(libs.ktor.logging)
            implementation(libs.ktor.darwin)
        }

    }
}

dependencies {
    add("kspIosX64", libs.kotlinInject.compiler)
    add("kspIosArm64", libs.kotlinInject.compiler)
}

android {
    namespace = "com.thomaskioko.trakt.service.implementation"
}