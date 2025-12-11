plugins {
    alias(libs.plugins.app.kmp)
}

scaffold {
    explicitApi()
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
