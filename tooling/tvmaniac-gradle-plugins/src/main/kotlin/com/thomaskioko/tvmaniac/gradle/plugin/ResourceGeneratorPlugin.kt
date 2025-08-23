package com.thomaskioko.tvmaniac.gradle.plugin

import com.thomaskioko.tvmaniac.gradle.tasks.MokoResourceGeneratorTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

public class ResourceGeneratorPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        val generateStringsTask = target.tasks.register("generateMokoStrings", MokoResourceGeneratorTask::class.java) { task ->
            task.group = "build"
            task.description = "Generates resource sealed class from Moko resources"
        }

        target.afterEvaluate {
            val generateMRTask = it.tasks.findByName("generateMRcommonMain")
            if (generateMRTask != null) {
                generateStringsTask.configure { task -> task.dependsOn(generateMRTask) }
            }
        }

        target.tasks.withType(KotlinCompile::class.java).configureEach { task ->
            task.dependsOn(generateStringsTask)
        }

        target.pluginManager.withPlugin("org.jetbrains.kotlin.multiplatform") {
            target.extensions.configure(KotlinMultiplatformExtension::class.java) { kotlin ->
                kotlin.sourceSets.named("commonMain") { sourceSet ->
                    sourceSet.kotlin.srcDir(generateStringsTask.map { it.commonMainOutput })
                }
            }
        }
    }
}
