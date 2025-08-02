plugins {
    alias(libs.plugins.tvmaniac.kmp)
}

tvmaniac {
    addAndroidMultiplatformTarget(withJava = true)
    useKotlinInject()
    useSerialization()

    optIn(
        "kotlinx.coroutines.InternalCoroutinesApi",
        "kotlinx.coroutines.ExperimentalCoroutinesApi",
    )
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.core.view)

            implementation(libs.coroutines.core)
            implementation(libs.decompose.decompose)
            implementation(libs.bundles.kotlinInject)
        }
    }
}
