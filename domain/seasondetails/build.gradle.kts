plugins {
    alias(libs.plugins.app.kmp)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.core.base)
                implementation(projects.data.cast.api)
                implementation(projects.data.seasondetails.api)

                api(libs.coroutines.core)

                implementation(libs.kotlinInject.runtime)
            }
        }
    }
}
