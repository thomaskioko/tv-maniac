plugins {
    alias(libs.plugins.tvmaniac.kmp)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.core.base)
                implementation(projects.data.similar.api)

                api(libs.coroutines.core)

                implementation(libs.metro.runtime)
            }
        }
    }
}
