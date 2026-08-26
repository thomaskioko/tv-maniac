package com.thomaskioko.tvmaniac.core.files.implementation

import com.thomaskioko.tvmaniac.core.files.testing.FakeFileManager
import io.kotest.matchers.shouldBe
import kotlinx.serialization.Serializable
import kotlin.test.Test

class DefaultJsonFileManagerTest {

    private val directory = "/containers/group.com.thomaskioko.tvmaniac"
    private val fileName = "sample.json"
    private val fileManager = FakeFileManager()
    private val jsonFileManager = DefaultJsonFileManager(fileManager)

    @Test
    fun `should return the value that was written`() {
        val value = Sample(id = 1396, name = "Breaking Bad", note = "Pilot")

        jsonFileManager.writeToFile(directory, fileName, value, Sample::class)

        jsonFileManager.getFileContent(directory, fileName, Sample::class) shouldBe value
    }

    @Test
    fun `should return null given no file was written`() {
        jsonFileManager.getFileContent(directory, fileName, Sample::class) shouldBe null
    }

    @Test
    fun `should return the value given the file has an unknown field`() {
        fileManager.writeToFile(
            directoryPath = directory,
            fileName = fileName,
            contents = """{"id":1,"name":"Uno","somethingAddedLater":"value"}""",
        )

        jsonFileManager.getFileContent(directory, fileName, Sample::class) shouldBe Sample(id = 1, name = "Uno")
    }

    @Test
    fun `should overwrite the file given one was written earlier`() {
        jsonFileManager.writeToFile(directory, fileName, Sample(id = 1, name = "First"), Sample::class)
        jsonFileManager.writeToFile(directory, fileName, Sample(id = 2, name = "Second"), Sample::class)

        jsonFileManager.getFileContent(directory, fileName, Sample::class) shouldBe Sample(id = 2, name = "Second")
    }

    @Serializable
    data class Sample(
        val id: Long,
        val name: String,
        val note: String? = null,
    )
}
