plugins {
    alias(libs.plugins.app.kmp)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(libs.coroutines.core)
            api(libs.store5)

            implementation(projects.core.base)
            implementation(projects.core.connectivity.api)
            implementation(libs.ktor.core)
        }

        commonTest.dependencies {
            implementation(libs.bundles.unittest)
            implementation(libs.ktor.mock)
            implementation(libs.ktor.negotiation)
            implementation(libs.ktor.serialization.json)
            implementation(libs.kotlinx.serialization.json)
            implementation(projects.core.base)
        }
    }
}
