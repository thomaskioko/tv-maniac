plugins {
    alias(libs.plugins.app.kmp)
}

scaffold {
    addAndroidMultiplatformTarget(withDeviceTestBuilder = true)
    useKotlinInject()
    useSerialization()

    optIn(
        "kotlinx.coroutines.ExperimentalCoroutinesApi",
        "kotlin.time.ExperimentalTime",
    )
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.base)
            implementation(projects.core.logger.api)
            implementation(libs.coroutines.core)
            implementation(libs.kermit)
            implementation(libs.kotlinx.datetime)
            implementation(libs.ktor.core)
            implementation(libs.yamlkt)
        }

        commonTest.dependencies { implementation(libs.bundles.unittest) }
    }
}
