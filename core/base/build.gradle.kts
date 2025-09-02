plugins {
    alias(libs.plugins.app.kmp)
}

scaffold {
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
        }
    }
}

/**
 * Workaround for Gradle 9.0 implicit dependency issue, The extractAndroidMainAnnotations task
 * uses output from kspAndroidMain,but doesn't declare an explicit dependency, causing build failures
 */
tasks.configureEach {
    if (name == "extractAndroidMainAnnotations") {
        dependsOn("kspAndroidMain")
    }
}
