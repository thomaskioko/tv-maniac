
import org.apache.tools.ant.taskdefs.condition.Os
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.tasks.CInteropProcess
import org.jetbrains.kotlin.konan.target.KonanTarget
import util.libs

plugins {
    `kmm-domain-plugin`
}

android {
    namespace = "com.thomaskioko.tvmaniac.shared.core.firebaseconfig.implementation"
}

dependencies {

    androidMainImplementation(platform(libs.firebase.bom))
    androidMainImplementation(libs.kotlinx.coroutines.playservices)
    androidMainImplementation(libs.firebase.config)

    commonMainImplementation(project(":shared:core:firebase-config:api"))
    commonMainImplementation(libs.kermit)
}

val KonanTarget.archVariant: String
    get() = if (this is KonanTarget.IOS_X64 || this is KonanTarget.IOS_SIMULATOR_ARM64) {
        "ios-arm64_i386_x86_64-simulator"
    } else {
        "ios-arm64_armv7"
    }

kotlin {

    fun nativeTargetConfig(): KotlinNativeTarget.() -> Unit = {
        val nativeFrameworkPaths = listOf(
            "FirebaseRemoteConfig"
        ).map {
            projectDir.resolve("src/nativeInterop/cinterop/Carthage/Build/$it.xcframework/${konanTarget.archVariant}")
        }

        binaries {
            getTest("DEBUG").apply {
                linkerOpts(nativeFrameworkPaths.map { "-F$it" })
                linkerOpts("-ObjC")
            }
        }

        compilations.getByName("main") {
            cinterops.create("FirebaseRemoteConfig") {
                compilerOpts(nativeFrameworkPaths.map { "-F$it" })
                extraOpts = listOf("-compiler-option", "-DNS_FORMAT_ARGUMENT(A)=", "-verbose")
            }
        }
    }
    ios(configure = nativeTargetConfig())
}

dependencies {

    commonMainImplementation(libs.koin.core)

    iosMainImplementation(libs.koin.core)

    val coroutineCore = libs.kotlin.coroutines.core.get()

    @Suppress("UnstableApiUsage")
    iosMainImplementation("${coroutineCore.module.group}:${coroutineCore.module.name}:${coroutineCore.versionConstraint.displayName}") {
        version {
            strictly(libs.versions.coroutines.native.get())
        }
    }
}

tasks {
    val carthageTasks = if (projectDir.resolve("src/nativeInterop/cinterop/Cartfile").exists()) {
        listOf("bootstrap", "update").map {
            task<Exec>("carthage${it.capitalize()}") {
                group = "carthage"
                executable = "carthage"
                args(
                    it,
                    "--project-directory", projectDir.resolve("src/nativeInterop/cinterop"),
                    "--platform", "iOS"
                )
            }
        }
    } else emptyList()

    if (Os.isFamily(Os.FAMILY_MAC)) {
        withType(CInteropProcess::class) {
            if (carthageTasks.isNotEmpty()) {
                dependsOn("carthageBootstrap")
            }
        }
    }

    create("carthageClean", Delete::class.java) {
        group = "carthage"
        delete(
            projectDir.resolve("src/nativeInterop/cinterop/Carthage"),
            projectDir.resolve("src/nativeInterop/cinterop/Cartfile.resolved")
        )
    }
}
