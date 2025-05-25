package com.thomaskioko.tvmaniac.gradle.plugin

import com.thomaskioko.tvmaniac.gradle.tasks.MokoResourceGeneratorTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

public class ResourceGeneratorPlugin : Plugin<Project> {
  override fun apply(target: Project) {
    target.afterEvaluate { project ->
      val generateStringsTask = project.tasks.register("generateMokoStrings", MokoResourceGeneratorTask::class.java) { task ->
        task.group = "build"
        task.description = "Generates resource sealed class from Moko resources"
      }

      val generateMRTask = project.tasks.findByName("generateMRcommonMain")
      if (generateMRTask != null) {
        generateStringsTask.configure { it.dependsOn(generateMRTask) }
      }

      project.tasks.withType(KotlinCompile::class.java).configureEach { task ->
        task.dependsOn(generateStringsTask)
      }

      // Configure source sets to include generated output
      project.extensions.configure(KotlinMultiplatformExtension::class.java) { kotlin ->
        kotlin.sourceSets.getByName("commonMain").kotlin.srcDir(generateStringsTask.get().commonMainOutput)
      }
    }
  }
}

