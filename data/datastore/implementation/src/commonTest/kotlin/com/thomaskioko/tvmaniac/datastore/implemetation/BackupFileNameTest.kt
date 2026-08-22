package com.thomaskioko.tvmaniac.datastore.implemetation

import com.thomaskioko.tvmaniac.datastore.api.BackupFileName
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import kotlin.test.Test

internal class BackupFileNameTest {

    @Test
    fun `should add the extension given a bare name`() {
        BackupFileName.sanitize("my shows") shouldBe "my shows.json"
    }

    @Test
    fun `should keep the extension given the name already has one`() {
        BackupFileName.sanitize("my shows.json") shouldBe "my shows.json"
    }

    @Test
    fun `should trim the name given it is padded`() {
        BackupFileName.sanitize("  my shows  ") shouldBe "my shows.json"
    }

    @Test
    fun `should refuse the name given it is empty`() {
        BackupFileName.sanitize("   ").shouldBeNull()
    }

    @Test
    fun `should refuse the name given it would reach outside the folder`() {
        BackupFileName.sanitize("../escape").shouldBeNull()
        BackupFileName.sanitize("nested/name").shouldBeNull()
        BackupFileName.sanitize("back\\slash").shouldBeNull()
    }
}
