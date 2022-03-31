import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("com.autonomousapps.dependency-analysis") version ("1.0.0-rc05")
}

buildscript {
    repositories.applyDefault()
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.6.10")
    }
}

allprojects {
    repositories.applyDefault()

    plugins.apply("checks.dependency-updates")
    plugins.apply("checks.detekt")
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}

subprojects {
    tasks.withType<KotlinCompile>().configureEach {
        with(kotlinOptions) {
            freeCompilerArgs = freeCompilerArgs + listOf(
                "-Xopt-in=kotlin.RequiresOptIn",
                "-Xopt-in=kotlin.OptIn",
                "-Xopt-in=kotlin.time.ExperimentalTime",
                "-Xopt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
            )
        }
    }
}

/**
 * Disable iosTest Task for now. Using mockk causes the build to fail. Revisit later.
 * Action:
 * - Resolve issue or replace dependency
 */
project.gradle.startParameter.excludedTaskNames.add("compileTestKotlinIos")

dependencyAnalysis {
    issues {
        all {
            ignoreKtx(true)

            onAny {
                severity("fail")
            }

            onRedundantPlugins {
                severity("fail")
            }

            onUnusedDependencies {
                exclude(
                    "org.jetbrains.kotlin:kotlin-stdlib-jdk8",
                    "androidx.core:core-ktx",
                    "javax.inject:javax.inject",
                    "com.google.dagger:hilt-compiler",
                )
            }

            onUsedTransitiveDependencies {
                exclude(
                    // added by the parcelize plugin
                    "org.jetbrains.kotlin:kotlin-parcelize-runtime",
                    "androidx.compose.runtime:runtime",
                )
            }

            onIncorrectConfiguration {
                exclude(
                    "javax.inject:javax.inject",
                    "com.google.dagger:hilt-compiler",
                    "androidx.compose.runtime:runtime",
                )
            }
        }
    }

    dependencies {

        bundle("kotlin-stdlib") {
            include("^org.jetbrains.kotlin:kotlin-stdlib.*")
        }

        bundle("androidx-compose-runtime") {
            includeGroup("androidx.compose.runtime")
        }

        bundle("androidx-compose-ui") {
            includeGroup("androidx.compose.ui")
        }

        bundle("androidx-compose-foundation") {
            includeGroup("androidx.compose.animation")
            includeGroup("androidx.compose.foundation")
        }

        bundle("androidx-compose-material") {
            includeGroup("androidx.compose.material")
        }

        bundle("androidx-lifecycle") {
            include("^androidx.lifecycle:lifecycle-common.*")
            include("^androidx.lifecycle:lifecycle-runtime.*")
        }

        bundle("coil") {
            includeDependency("io.coil-kt:coil-compose")
        }

        bundle("dagger") {
            includeDependency("javax.inject:javax.inject")
            includeDependency("com.google.dagger:hilt-android")
        }
    }
}
