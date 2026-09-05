package com.thomaskioko.tvmaniac.core.networkutil.api.extensions

import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class PagingExtensionsTest {

    @Test
    fun `should request every page until a short page`() = runTest {
        val requestedPages = mutableListOf<Int>()

        val result = fetchPages(limit = 3) { page, limit ->
            requestedPages += page
            val count = if (page < 3) limit else 1
            ApiResponse.Success(List(count) { "page$page-$it" })
        }

        requestedPages shouldBe listOf(1, 2, 3)
        result.shouldBeInstanceOf<ApiResponse.Success<List<String>>>().body.size shouldBe 7
    }

    @Test
    fun `should stop after one request given the first page is short`() = runTest {
        var requests = 0

        val result = fetchPages(limit = 3) { _, _ ->
            requests++
            ApiResponse.Success(listOf("a"))
        }

        requests shouldBe 1
        result.shouldBeInstanceOf<ApiResponse.Success<List<String>>>().body shouldBe listOf("a")
    }

    @Test
    fun `should return the failure given a later page fails`() = runTest {
        val result = fetchPages(limit = 2) { page, limit ->
            if (page == 1) {
                ApiResponse.Success(List(limit) { "item$it" })
            } else {
                ApiResponse.Error.HttpError(code = 429, errorBody = null, errorMessage = null)
            }
        }

        result.shouldBeInstanceOf<ApiResponse.Error.HttpError<*>>().code shouldBe 429
    }

    @Test
    fun `should start from the given first page`() = runTest {
        val requestedPages = mutableListOf<Int>()

        fetchPages(limit = 2, firstPage = 4) { page, _ ->
            requestedPages += page
            ApiResponse.Success(emptyList<String>())
        }

        requestedPages shouldBe listOf(4)
    }
}
