import org.gradle.api.tasks.PathSensitivity

plugins {
    alias(libs.plugins.app.kmp)
}

scaffold {
    useMetro()
    useSerialization()
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(libs.coroutines.core)
                api(projects.core.base)
                api(projects.core.files.api)
                api(projects.core.logger.api)
                api(projects.core.tasks.api)
                api(projects.core.util.api)
                api(projects.data.upnext.api)
            }
        }

        commonTest {
            dependencies {
                implementation(libs.bundles.unittest)
                implementation(libs.kotlinx.serialization.json)
                implementation(projects.core.files.implementation)
                implementation(projects.core.files.testing)
                implementation(projects.core.logger.testing)
                implementation(projects.core.tasks.testing)
                implementation(projects.core.util.testing)
                implementation(projects.data.upnext.testing)
            }
        }
    }
}

tasks.withType<Test>().configureEach {
    inputs.file(rootProject.file("ios/Packages/Models/Tests/ModelsTests/Fixtures/widget-snapshot.json"))
        .withPropertyName("widgetSnapshotFixture")
        .withPathSensitivity(PathSensitivity.RELATIVE)
}
