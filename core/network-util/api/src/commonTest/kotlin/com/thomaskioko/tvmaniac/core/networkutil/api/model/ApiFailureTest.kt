package com.thomaskioko.tvmaniac.core.networkutil.api.model

import io.kotest.matchers.shouldBe
import kotlin.test.Test

class ApiFailureTest {

    @Test
    fun `should classify 400 as unexpected`() {
        httpStatusToKind(400) shouldBe ApiFailureKind.Unexpected
    }

    @Test
    fun `should classify 401 as expected`() {
        httpStatusToKind(401) shouldBe ApiFailureKind.Expected
    }

    @Test
    fun `should classify 403 as unexpected`() {
        httpStatusToKind(403) shouldBe ApiFailureKind.Unexpected
    }

    @Test
    fun `should classify 404 as expected`() {
        httpStatusToKind(404) shouldBe ApiFailureKind.Expected
    }

    @Test
    fun `should classify 408 as expected`() {
        httpStatusToKind(408) shouldBe ApiFailureKind.Expected
    }

    @Test
    fun `should classify 420 as expected`() {
        httpStatusToKind(420) shouldBe ApiFailureKind.Expected
    }

    @Test
    fun `should classify 429 as expected`() {
        httpStatusToKind(429) shouldBe ApiFailureKind.Expected
    }

    @Test
    fun `should classify 500 as unexpected`() {
        httpStatusToKind(500) shouldBe ApiFailureKind.Unexpected
    }

    @Test
    fun `should classify 503 as unexpected`() {
        httpStatusToKind(503) shouldBe ApiFailureKind.Unexpected
    }

    @Test
    fun `should replace numeric path segments with id`() {
        "/shows/1234/seasons/5".toEndpointTemplate() shouldBe "/shows/{id}/seasons/{id}"
    }

    @Test
    fun `should strip the query string from the endpoint template`() {
        "/shows/1234?access_token=secret".toEndpointTemplate() shouldBe "/shows/{id}"
    }

    @Test
    fun `should leave non numeric segments untouched`() {
        "/shows/trending".toEndpointTemplate() shouldBe "/shows/trending"
    }

    @Test
    fun `should build a short message with the status when present`() {
        val failure = ApiFailure(
            provider = "trakt",
            method = "GET",
            endpointTemplate = "/shows/{id}",
            status = 500,
            kind = ApiFailureKind.Unexpected,
            body = null,
        )

        failure.toLogMessage() shouldBe "HTTP 500 GET /shows/{id}"
    }

    @Test
    fun `should append the body to the message when present`() {
        val failure = ApiFailure(
            provider = "trakt",
            method = "GET",
            endpointTemplate = "shows/{id}",
            status = 500,
            kind = ApiFailureKind.Unexpected,
            body = "upstream down",
        )

        failure.toLogMessage() shouldBe "HTTP 500 GET shows/{id}: upstream down"
    }

    @Test
    fun `should build a short message without a status when absent`() {
        val failure = ApiFailure(
            provider = "trakt",
            method = "GET",
            endpointTemplate = "/shows/{id}",
            status = null,
            kind = ApiFailureKind.Expected,
            body = null,
        )

        failure.toLogMessage() shouldBe "GET /shows/{id} failed"
    }
}
