plugins {
    id("java-gradle-plugin")
    id("org.jetbrains.kotlin.jvm")
}

tasks {
    validatePlugins {
        enableStricterValidation = true
        failOnWarning = true
    }

    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
        compilerOptions {
            allWarningsAsErrors.set(false)
            freeCompilerArgs.addAll(
                "-opt-in=kotlin.RequiresOptIn",
                "-Xjvm-default=all"
            )
        }
    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(libs.versions.jdk.get()))
    }
}

kotlin {
    explicitApi()
}

dependencies {
    api(libs.kotlin.gradle.plugin)
    implementation(libs.android.gradle.plugin)
    implementation(libs.moko.resources.generator)
    implementation(libs.compose.compiler.gradle.plugin)
    implementation(libs.kotlin.gradle.plugin.api)
    implementation(libs.dependency.analysis.gradle.plugin)
    implementation(libs.kotlinpoet.ksp)
    implementation(libs.moko.resources)

    compileOnly(libs.baselineprofile.gradlePlugin)
    compileOnly(libs.skie.gradle.plugin)
    compileOnly(libs.spotless.plugin)
    implementation(libs.gradle.doctor.gradle.plugin)
    runtimeOnly(libs.compose.compiler.gradle.plugin)
}

gradlePlugin {
    plugins {
        create("appPlugin") {
            id = "com.thomaskioko.tvmaniac.gradle.app"
            implementationClass = "com.thomaskioko.tvmaniac.gradle.plugin.AppPlugin"
        }

        create("androidPlugin") {
            id = "com.thomaskioko.tvmaniac.gradle.android"
            implementationClass = "com.thomaskioko.tvmaniac.gradle.plugin.AndroidPlugin"
        }

        create("androidMultiplatformPlugin") {
            id = "com.thomaskioko.tvmaniac.gradle.android.multiplatform"
            implementationClass = "com.thomaskioko.tvmaniac.gradle.plugin.AndroidMultiplatformPlugin"
        }

        create("jvmPlugin") {
            id = "com.thomaskioko.tvmaniac.gradle.jvm"
            implementationClass = "com.thomaskioko.tvmaniac.gradle.plugin.JvmPlugin"
        }

        create("baselineProfilePlugin") {
            id = "com.thomaskioko.tvmaniac.gradle.baseline.profile"
            implementationClass = "com.thomaskioko.tvmaniac.gradle.plugin.BaselineProfilePlugin"
        }

        create("commonMultiplatformPlugin") {
            id = "com.thomaskioko.tvmaniac.gradle.multiplatform"
            implementationClass = "com.thomaskioko.tvmaniac.gradle.plugin.KotlinMultiplatformPlugin"
        }

        create("basePlugin") {
            id = "com.thomaskioko.tvmaniac.gradle.base"
            implementationClass = "com.thomaskioko.tvmaniac.gradle.plugin.BasePlugin"
        }

        create("rootPlugin") {
            id = "com.thomaskioko.tvmaniac.gradle.root"
            implementationClass = "com.thomaskioko.tvmaniac.gradle.plugin.RootPlugin"
        }

        create("spotlessPlugin") {
            id = "com.thomaskioko.tvmaniac.gradle.spotless"
            implementationClass = "com.thomaskioko.tvmaniac.gradle.plugin.checks.SpotlessPlugin"
        }

        create("resourceGeneratorPlugin") {
            id = "com.thomaskioko.tvmaniac.resource.generator"
            implementationClass = "com.thomaskioko.tvmaniac.gradle.plugin.ResourceGeneratorPlugin"
        }
    }
}
