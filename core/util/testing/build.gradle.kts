plugins {
    alias(libs.plugins.app.kmp)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(projects.core.util)
                api(projects.core.util.api)
                implementation(projects.core.base)
            }
        }
    }
}
