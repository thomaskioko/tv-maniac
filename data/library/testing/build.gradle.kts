plugins {
    alias(libs.plugins.app.kmp)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(projects.data.library.api)

                implementation(libs.coroutines.core)
            }
        }
    }
}
