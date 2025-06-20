plugins {
    alias(libs.plugins.tvmaniac.kmp)
}

tvmaniac {
    addAndroidMultiplatformTarget()
    useKotlinInjectAnvilCompiler()

    optIn(
        "kotlinx.coroutines.ExperimentalCoroutinesApi",
        "kotlinx.coroutines.InternalCoroutinesApi",
    )
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.core.base)
                implementation(projects.data.datastore.api)

                api(libs.androidx.datastore.preference)
            }
        }

        commonTest { dependencies { implementation(libs.bundles.unittest) } }
    }
}
