plugins {
    alias(libs.plugins.tvmaniac.kmp)
}

tvmaniac {
    useKotlinInjectAnvilCompiler()

    optIn(
        "kotlinx.coroutines.ExperimentalCoroutinesApi",
    )
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.data.database.sqldelight)
                api(libs.kotlinx.datetime)
            }
        }
    }
}
