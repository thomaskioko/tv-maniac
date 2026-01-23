plugins {
    alias(libs.plugins.app.kmp)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(projects.data.database.sqldelight)
                api(projects.data.shows.api)
                implementation(projects.core.networkUtil.api)

                implementation(projects.core.base)

                api(libs.androidx.paging.common)
                api(libs.coroutines.core)

                implementation(libs.kotlinInject.runtime)
            }
        }
    }
}
