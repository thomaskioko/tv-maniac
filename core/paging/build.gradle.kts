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
            implementation(projects.data.shows.api)
            implementation(libs.coroutines.core)
            implementation(libs.androidx.paging.common)
            implementation(libs.sqldelight.primitive.adapters)
        }
    }
}
