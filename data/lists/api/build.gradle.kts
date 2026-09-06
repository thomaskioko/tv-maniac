plugins {
    alias(libs.plugins.app.kmp)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(libs.androidx.paging.common)
                api(libs.coroutines.core)
            }
        }
    }
}
