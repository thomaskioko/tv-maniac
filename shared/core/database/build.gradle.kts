plugins {
    id("tvmaniac.kmm.library")
    alias(libs.plugins.sqldelight)
    alias(libs.plugins.ksp)
}


kotlin {
    android()
    ios()

    sourceSets {

        sourceSets["androidMain"].dependencies {
            implementation(libs.sqldelight.driver.android)
        }

        sourceSets["commonMain"].dependencies {
            implementation(projects.shared.core.util)
            implementation(libs.sqldelight.primitive.adapters)
            implementation(libs.kotlinInject.runtime)
        }


        sourceSets["androidTest"].dependencies {
            implementation(kotlin("test"))
            implementation(libs.sqldelight.driver.jvm)
        }


        sourceSets["commonTest"].dependencies {
            implementation(kotlin("test"))
            implementation(libs.kotest.assertions)
        }

        sourceSets["iosMain"].dependencies {
            implementation(libs.sqldelight.driver.native)
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
