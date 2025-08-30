plugins {
    alias(libs.plugins.tvmaniac.kmp)
}

tvmaniac {
    addAndroidMultiplatformTarget()
    useDependencyInjection()
    useSerialization()

    optIn(
        "kotlinx.coroutines.InternalCoroutinesApi",
    )
}

kotlin {
    sourceSets {
        commonMain.dependencies {

            implementation(projects.core.base)

            implementation(libs.androidx.paging.common)
            implementation(libs.coroutines.core)
            implementation(libs.ktor.core)
            implementation(libs.store5)
        }
    }
}
