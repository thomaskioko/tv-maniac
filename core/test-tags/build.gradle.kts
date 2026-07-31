plugins {
    alias(libs.plugins.app.kmp)
}

scaffold {
    addAndroidTarget()
}

kotlin {
    sourceSets {
        jvmTest {
            dependencies {
                implementation(libs.bundles.unittest)
            }
        }
    }
}

val iosTestTags = rootProject.layout.projectDirectory.file("ios/tvmaniacUITests/TestTags.swift")

tasks.withType<Test>().configureEach {
    systemProperty("iosTestTagsFile", iosTestTags.asFile.absolutePath)
    systemProperty("sharedTestTagsDir", layout.projectDirectory.dir("src/commonMain/kotlin").asFile.absolutePath)
    inputs.file(iosTestTags).withPathSensitivity(PathSensitivity.RELATIVE)
}
