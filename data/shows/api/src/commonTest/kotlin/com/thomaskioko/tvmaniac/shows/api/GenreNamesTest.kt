package com.thomaskioko.tvmaniac.shows.api

import io.kotest.matchers.shouldBe
import kotlin.test.Test

internal class GenreNamesTest {

    @Test
    fun `should title case a single word slug`() {
        traktGenreName("drama") shouldBe "Drama"
    }

    @Test
    fun `should turn a hyphenated slug into separate words`() {
        traktGenreName("science-fiction") shouldBe "Science Fiction"
        traktGenreName("talk-show") shouldBe "Talk Show"
        traktGenreName("home-and-garden") shouldBe "Home And Garden"
    }

    @Test
    fun `should keep a TMDB genre that already matches Trakt`() {
        traktGenreNames("Drama") shouldBe listOf("Drama")
        traktGenreNames("Western") shouldBe listOf("Western")
    }

    @Test
    fun `should split the TMDB genres that cover two Trakt genres`() {
        traktGenreNames("Action & Adventure") shouldBe listOf("Action", "Adventure")
        traktGenreNames("Sci-Fi & Fantasy") shouldBe listOf("Science Fiction", "Fantasy")
    }

    @Test
    fun `should rename the TMDB genres Trakt calls something else`() {
        traktGenreNames("War & Politics") shouldBe listOf("War")
        traktGenreNames("Kids") shouldBe listOf("Children")
        traktGenreNames("Talk") shouldBe listOf("Talk Show")
    }

    @Test
    fun `should give the same name whichever source a genre came from`() {
        traktGenreNames("Sci-Fi & Fantasy").first() shouldBe traktGenreName("science-fiction")
        traktGenreNames("Kids").single() shouldBe traktGenreName("children")
        traktGenreNames("Talk").single() shouldBe traktGenreName("talk-show")
    }
}
