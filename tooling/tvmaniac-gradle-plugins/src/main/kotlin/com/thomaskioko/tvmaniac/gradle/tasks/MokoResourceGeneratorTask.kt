package com.thomaskioko.tvmaniac.gradle.tasks

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import java.io.File
import javax.inject.Inject
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.ProjectLayout
import org.gradle.api.model.ObjectFactory
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

@CacheableTask
public abstract class MokoResourceGeneratorTask
@Inject constructor(
    objectFactory: ObjectFactory,
    layout: ProjectLayout,
) : DefaultTask() {

    @get:OutputDirectory
    public val commonMainOutput: DirectoryProperty = objectFactory.directoryProperty()
        .convention(layout.buildDirectory.dir("generated/resources"))

    @TaskAction
    public fun generate() {
        logger.lifecycle("Starting GenerateMokoStringsTask")

        val outputDir = commonMainOutput.get().asFile
        logger.lifecycle("Output directory: ${outputDir.absolutePath}")

        outputDir.deleteRecursively()
        outputDir.mkdirs()

        val mrClass = ClassName("com.thomaskioko.tvmaniac.i18n", "MR")
        val mrFile = project.file("build/generated/moko-resources/commonMain/src/com/thomaskioko/tvmaniac/i18n/MR.kt")

        if (!mrFile.exists()) {
            logger.warn("MR.kt file not found at ${mrFile.absolutePath}")
            return
        }

        val (stringKeys, pluralKeys) = readKeysFromMRFile(mrFile)
        logger.lifecycle("Found ${stringKeys.size} string keys and ${pluralKeys.size} plural keys")

        // Generate StringResourceKey sealed class
        val resourceKeyFile = File(outputDir, "StringResourceKey.kt")
        logger.lifecycle("Generating StringResourceKey.kt at: ${resourceKeyFile.absolutePath}")
        stringResourceKeyFileSpec(
            stringKeys = stringKeys,
            mrClass = mrClass,
        ).writeTo(outputDir)

        // Generate PluralsResourceKey sealed class
        val pluralsResourceKeyFile = File(outputDir, "PluralsResourceKey.kt")
        logger.lifecycle("Generating PluralsResourceKey.kt at: ${pluralsResourceKeyFile.absolutePath}")
        pluralsResourceKeyFileSpec(
            pluralKeys = pluralKeys,
            mrClass = mrClass,
        ).writeTo(outputDir)

        logger.lifecycle("GenerateMokoStringsTask completed successfully")
    }

    private fun readKeysFromMRFile(mrFile: File): Pair<List<String>, List<String>> {
        val stringKeys = mutableListOf<String>()
        val pluralKeys = mutableListOf<String>()
        var isInStringsObject = false
        var isInPluralsObject = false

        mrFile.readLines().forEach { line ->
            when {
                line.contains("object strings") -> {
                    isInStringsObject = true
                    isInPluralsObject = false
                }

                line.contains("object plurals") -> {
                    isInStringsObject = false
                    isInPluralsObject = true
                }

                line.trim() == "}" -> {
                    isInStringsObject = false
                    isInPluralsObject = false
                }

                isInStringsObject && line.contains("public val") && line.contains(": StringResource") -> {
                    extractKeyName(line)?.let { stringKeys.add(it) }
                }

                isInPluralsObject && line.contains("public val") && line.contains(": PluralsResource") -> {
                    extractKeyName(line)?.let { pluralKeys.add(it) }
                }
            }
        }
        return stringKeys to pluralKeys
    }

    private fun extractKeyName(line: String): String? {
        // Example: public val button_error_retry: StringResource
        return line.split("public val")
            .getOrNull(1)
            ?.trim()
            ?.split(":")
            ?.getOrNull(0)
            ?.trim()
    }

    private fun toPascalCase(name: String): String {
        return name.split('_').joinToString("") { it.replaceFirstChar { c -> c.uppercaseChar() } }
    }

    private fun stringResourceKeyFileSpec(
        stringKeys: List<String>,
        mrClass: ClassName,
    ): FileSpec = FileSpec.builder("com.thomaskioko.tvmaniac.i18n", "StringResourceKey")
        .addType(
            TypeSpec.classBuilder("StringResourceKey")
                .addModifiers(KModifier.SEALED)
                .primaryConstructor(
                    FunSpec.constructorBuilder()
                        .addParameter("resourceId", ClassName("dev.icerock.moko.resources", "StringResource"))
                        .build(),
                )
                .addProperty(
                    PropertySpec.builder("resourceId", ClassName("dev.icerock.moko.resources", "StringResource"))
                        .initializer("resourceId")
                        .build(),
                )
                .addTypes(
                    stringKeys.map { key ->
                        TypeSpec.objectBuilder(toPascalCase(key))
                            .addModifiers(KModifier.DATA)
                            .superclass(ClassName("com.thomaskioko.tvmaniac.i18n", "StringResourceKey"))
                            .addSuperclassConstructorParameter("%T.strings.%N", mrClass, key)
                            .build()
                    },
                )
                .build(),
        )
        .build()

    private fun pluralsResourceKeyFileSpec(
        pluralKeys: List<String>,
        mrClass: ClassName,
    ): FileSpec = FileSpec.builder("com.thomaskioko.tvmaniac.i18n", "PluralsResourceKey")
        .addType(
            TypeSpec.classBuilder("PluralsResourceKey")
                .addModifiers(KModifier.SEALED)
                .primaryConstructor(
                    FunSpec.constructorBuilder()
                        .addParameter("resourceId", ClassName("dev.icerock.moko.resources", "PluralsResource"))
                        .build(),
                )
                .addProperty(
                    PropertySpec.builder("resourceId", ClassName("dev.icerock.moko.resources", "PluralsResource"))
                        .initializer("resourceId")
                        .build(),
                )
                .addTypes(
                    pluralKeys.map { key ->
                        TypeSpec.objectBuilder(toPascalCase(key))
                            .addModifiers(KModifier.DATA)
                            .superclass(ClassName("com.thomaskioko.tvmaniac.i18n", "PluralsResourceKey"))
                            .addSuperclassConstructorParameter("%T.plurals.%N", mrClass, key)
                            .build()
                    },
                )
                .build(),
        )
        .build()

}
