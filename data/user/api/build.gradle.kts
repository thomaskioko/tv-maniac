plugins {
    alias(libs.plugins.app.kmp)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(projects.data.database.sqldelight)
                api(projects.core.networkUtil)

                implementation(projects.core.base)

                api(libs.coroutines.core)
                implementation(libs.kotlinInject.runtime)
            }
        }
    }
}
