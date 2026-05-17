plugins {
    alias(libs.plugins.app.kmp)
}

scaffold {
    optIn(
        "androidx.paging.ExperimentalPagingApi",
    )
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(libs.androidx.paging.common)
            api(libs.sqldelight.runtime)
            implementation(projects.data.shows.api)
            implementation(libs.coroutines.core)
        }
    }
}
