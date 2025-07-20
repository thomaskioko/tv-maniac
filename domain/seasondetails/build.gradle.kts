plugins {
    alias(libs.plugins.tvmaniac.kmp)
}

tvmaniac {
    useDependencyInjection()
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.core.base)
                implementation(projects.data.cast.api)
                implementation(projects.data.seasondetails.api)

                api(libs.coroutines.core)
            }
        }
    }
}
