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
                implementation(projects.core.base)
            }
        }
    }
}
