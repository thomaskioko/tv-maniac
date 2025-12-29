plugins {
    alias(libs.plugins.app.kmp)
}

scaffold {
    explicitApi()
    optIn(
        "kotlinx.coroutines.InternalCoroutinesApi",
        "kotlin.uuid.ExperimentalUuidApi",
    )
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.core.logger.api)
                implementation(libs.kotlinx.atomicfu)
                implementation(libs.coroutines.core)
            }
        }
    }
}
