plugins {
    alias(libs.plugins.app.kmp)
}

scaffold {
    useKotlinInject()
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.api.tmdb.api)
                implementation(projects.core.base)
                implementation(projects.data.episodes.api)
                implementation(projects.data.shows.api)

                implementation(libs.sqldelight.extensions)
            }
        }

        commonTest {
            dependencies {
                implementation(projects.data.database.testing)
                implementation(libs.bundles.unittest)
            }
        }
    }
}
