plugins {
    alias(libs.plugins.tvmaniac.kmp)
}

tvmaniac {
    multiplatform {
        addAndroidMultiplatformTarget(withJava = true)
        useKotlinInjectAnvilCompiler()
        useSerialization()
    }

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
