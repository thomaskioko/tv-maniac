plugins {
    alias(libs.plugins.app.kmp)
}

scaffold {
    explicitApi()
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.core.base)
                implementation(projects.data.cast.api)
                implementation(projects.data.seasondetails.api)

                api(libs.coroutines.core)
                api(libs.kotlinx.collections)

                implementation(libs.kotlinInject.runtime)
            }
        }
    }
}
