package com.thomaskioko.tvmaniac.genre

import com.thomaskioko.tvmaniac.genre.model.GenreShowCategory
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlin.test.Test

internal class GenreShowsRequestIdTest {

    @Test
    fun `should differ given the page differs`() {
        val slug = "drama"
        val category = GenreShowCategory.POPULAR

        val page1 = genreShowsRequestId(slug, category, page = 1L)
        val page2 = genreShowsRequestId(slug, category, page = 2L)

        page1 shouldNotBe page2
    }

    @Test
    fun `should be stable given the same slug category and page`() {
        val id = genreShowsRequestId("drama", GenreShowCategory.POPULAR, page = 1L)

        genreShowsRequestId("drama", GenreShowCategory.POPULAR, page = 1L) shouldBe id
    }
}
