plugins {
    alias(libs.plugins.tvmaniac.kmp)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(projects.data.shows.api)
                api(projects.data.database.sqldelight)

                implementation(projects.core.base)

                api(libs.androidx.paging.common)
                api(libs.coroutines.core)

                implementation(libs.metro.runtime)
            }
        }
    }
}
