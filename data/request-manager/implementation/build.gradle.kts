plugins {
    alias(libs.plugins.tvmaniac.kmp)
}

tvmaniac {
    useKotlinInjectAnvilCompiler()
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.core.base)
                implementation(projects.data.database.sqldelight)
                implementation(projects.data.requestManager.api)

                implementation(libs.kotlinx.datetime)
                implementation(libs.sqldelight.extensions)
            }
        }

        commonTest {
            dependencies {
                implementation(projects.data.database.testing)

                implementation(libs.kotlin.test)
                implementation(libs.kotest.assertions)
            }
        }
    }
}
