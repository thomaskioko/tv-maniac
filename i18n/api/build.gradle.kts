plugins {
    alias(libs.plugins.tvmaniac.kmp)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(projects.i18n.generator)
            }
        }
    }
}
