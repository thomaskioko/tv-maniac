plugins {
    alias(libs.plugins.app.kmp)
}

scaffold {
    optIn(
        "kotlinx.coroutines.InternalCoroutinesApi",
        "kotlin.uuid.ExperimentalUuidApi",
    )
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(projects.core.logger.api)
                api(libs.coroutines.core)
                api(libs.kotlinx.collections)
                implementation(libs.kotlinx.atomicfu)
            }
        }
    }
}
