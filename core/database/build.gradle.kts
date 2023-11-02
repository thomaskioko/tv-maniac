plugins {
    id("plugin.tvmaniac.android.library")
    id("plugin.tvmaniac.multiplatform")
    alias(libs.plugins.sqldelight)
    alias(libs.plugins.ksp)
}


kotlin {
    sourceSets {
        androidMain {
            dependencies {
                implementation(libs.sqldelight.driver.android)
            }
        }

        commonMain {
            dependencies {
                implementation(projects.core.util)
                implementation(libs.sqldelight.primitive.adapters)
                implementation(libs.kotlinInject.runtime)
                implementation(libs.kotlinx.datetime)
            }
        }

        androidUnitTest {
            dependencies {
                implementation(kotlin("test"))
                implementation(libs.sqldelight.driver.jvm)
            }
        }

        commonTest {
            dependencies {
                implementation(kotlin("test"))
                implementation(libs.kotest.assertions)
            }
        }

        iosMain {
            dependencies {
                implementation(libs.sqldelight.driver.native)
            }
        }

        jvmTest {
            dependencies {
                implementation(libs.sqldelight.driver.jvm)
            }
        }
    }
}

dependencies {
    add("kspIosX64", libs.kotlinInject.compiler)
    add("kspIosArm64", libs.kotlinInject.compiler)
}

android {
    namespace = "com.thomaskioko.tvmaniac.db"
}

sqldelight {
    databases {
        create("TvManiacDatabase") {
            packageName.set("com.thomaskioko.tvmaniac.core.db")
        }
    }
    linkSqlite.set(true)
}
