plugins {
    alias(libs.plugins.app.kmp)
}

scaffold {
    useSerialization()
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(projects.navigation.api)
                implementation(projects.core.base)
            }
        }
    }
}
