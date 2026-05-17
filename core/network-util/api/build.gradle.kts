plugins {
    alias(libs.plugins.app.kmp)
}

kotlin {
    sourceSets {
        androidMain.dependencies {
            implementation(libs.kotlin.serialization.core)
        }

        commonMain.dependencies {
            api(libs.coroutines.core)
            api(libs.ktor.core)
            api(libs.store5)
            api(projects.core.connectivity.api)
        }

        commonTest.dependencies {
            implementation(libs.bundles.unittest)
            implementation(libs.ktor.mock)
            implementation(libs.ktor.negotiation)
            implementation(libs.ktor.serialization.json)
            implementation(libs.kotlinx.serialization.json)
        }

        jvmTest.dependencies {
            implementation(libs.kotlin.serialization.core)
        }
    }
}
