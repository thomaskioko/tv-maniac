plugins {
    alias(libs.plugins.app.kmp)
}

scaffold {
    addAndroidMultiplatformTarget()
    useKotlinInject()
    useSerialization()

    optIn(
        "kotlinx.coroutines.InternalCoroutinesApi",
    )
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.core.networkUtil.api)

            implementation(projects.core.base)

            implementation(libs.coroutines.core)
            implementation(libs.kotlinx.atomicfu)
            implementation(libs.ktor.core)
            implementation(libs.store5)
        }

        commonTest.dependencies {
            implementation(libs.bundles.unittest)
        }
    }
}
