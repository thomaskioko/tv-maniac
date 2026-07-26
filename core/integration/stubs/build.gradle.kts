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

        iosMain.dependencies {
            api(projects.data.accountManager.api)
            api(projects.data.oauth.api)
            implementation(projects.data.database.sqldelight)
            implementation(projects.data.datastore.implementation)
        }

        commonTest.dependencies {
            implementation(libs.bundles.unittest)
        }
    }
}

// Kotlin/Native has no resource mechanism, so the iOS fixture loader reads from a directory named
// by an environment variable. Point it at the packaged resources for `iosTest`; XCUITest supplies
// its own value through the launch environment.
//
// The SIMCTL_CHILD_ prefix is required: simulator tests run under `xcrun simctl spawn`, which drops
// every variable that is not prefixed. The unprefixed name is set too so device and host targets,
// which spawn the binary directly, see it as well.
tasks.withType<org.jetbrains.kotlin.gradle.targets.native.tasks.KotlinNativeTest>().configureEach {
    val fixtureDir = layout.projectDirectory.dir("src/commonMain/resources").asFile.absolutePath
    environment("TVMANIAC_FIXTURE_DIR", fixtureDir)
    environment("SIMCTL_CHILD_TVMANIAC_FIXTURE_DIR", fixtureDir)
}
