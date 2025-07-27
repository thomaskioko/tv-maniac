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
                implementation(projects.data.watchproviders.api)

                api(libs.coroutines.core)
            }
        }
    }
}
