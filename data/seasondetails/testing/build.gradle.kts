plugins {
    id("plugin.tvmaniac.multiplatform")
}


kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.core.database)
                implementation(projects.core.util)
                implementation(projects.data.seasondetails.api)

                implementation(libs.coroutines.core)
            }
        }
    }
}
