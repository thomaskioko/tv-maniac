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
            api(projects.core.logger.api)
            implementation(projects.data.shows.api)
            implementation(libs.coroutines.core)
        }

        commonTest.dependencies {
            implementation(libs.bundles.unittest)
            implementation(projects.core.logger.testing)
        }
    }
}
