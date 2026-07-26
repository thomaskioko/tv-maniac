import org.jetbrains.kotlin.gradle.targets.native.tasks.KotlinNativeTest

plugins {
    alias(libs.plugins.app.kmp)
}

scaffold {
    addAndroidTarget(
        enableAndroidResources = true,
    )
}

kotlin {
    sourceSets {
        val jvmAndAndroidMain =
            create("jvmAndAndroidMain") {
                dependsOn(getByName("commonMain"))
            }
        getByName("jvmMain").dependsOn(jvmAndAndroidMain)
        getByName("androidMain").dependsOn(jvmAndAndroidMain)

        commonMain.dependencies {
            api(libs.ktor.core)
            api(libs.ktor.mock)
            implementation(libs.kotlinx.serialization.json)
        }

        commonTest.dependencies {
            implementation(libs.bundles.unittest)
        }
    }
}

tasks.withType<KotlinNativeTest>().configureEach {
    val fixtureDir = layout.projectDirectory.dir("src/commonMain/resources").asFile.absolutePath
    environment("TVMANIAC_FIXTURE_DIR", fixtureDir)
    environment("SIMCTL_CHILD_TVMANIAC_FIXTURE_DIR", fixtureDir)
}
