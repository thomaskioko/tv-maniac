package com.thomaskioko.tvmaniac.core.networkutil.api.model

import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlin.test.Test

class ThrowableToSyncErrorTest {

    @Test
    fun `should unwrap SyncException`() {
        val expected = SyncError.Permanent.NotFound("missing show")
        val actual = SyncException(expected).toSyncError()

        actual shouldBe expected
    }

    @Test
    fun `should classify ApiHttpException 401 as AuthenticationFailed`() {
        val actual = ApiHttpException(code = 401, message = "Unauthorized").toSyncError()

        actual.shouldBeInstanceOf<SyncError.Permanent.AuthenticationFailed>()
        actual.message shouldBe "Unauthorized"
    }

    @Test
    fun `should classify ApiHttpException 403 as Forbidden`() {
        val actual = ApiHttpException(code = 403, message = "Access forbidden.").toSyncError()

        actual.shouldBeInstanceOf<SyncError.Permanent.Forbidden>()
    }

    @Test
    fun `should classify ApiHttpException 404 as NotFound`() {
        val actual = ApiHttpException(code = 404, message = "Resource not found.").toSyncError()

        actual.shouldBeInstanceOf<SyncError.Permanent.NotFound>()
    }

    @Test
    fun `should classify ApiHttpException 429 as RateLimited`() {
        val actual = ApiHttpException(code = 429, message = "Too many requests").toSyncError()

        actual.shouldBeInstanceOf<SyncError.Retryable.RateLimited>()
    }

    @Test
    fun `should classify ApiHttpException 503 as ServerError`() {
        val actual = ApiHttpException(code = 503, message = "Service unavailable").toSyncError()

        val serverError = actual.shouldBeInstanceOf<SyncError.Retryable.ServerError>()
        serverError.statusCode shouldBe 503
    }

    @Test
    fun `should map ApiSerializationException to InvalidData`() {
        val actual = ApiSerializationException("malformed JSON").toSyncError()

        actual.shouldBeInstanceOf<SyncError.Permanent.InvalidData>()
        actual.message shouldBe "malformed JSON"
    }

    @Test
    fun `should classify ApiNetworkException Timeout as Retryable Timeout`() {
        val actual = ApiNetworkException(
            kind = ApiResponse.Error.NetworkFailure.Kind.Timeout,
            message = "timed out",
        ).toSyncError()

        actual.shouldBeInstanceOf<SyncError.Retryable.Timeout>()
    }

    @Test
    fun `should classify ApiNetworkException Connectivity as Retryable NetworkError`() {
        val actual = ApiNetworkException(
            kind = ApiResponse.Error.NetworkFailure.Kind.Connectivity,
            message = "no route",
        ).toSyncError()

        actual.shouldBeInstanceOf<SyncError.Retryable.NetworkError>()
    }

    @Test
    fun `should classify ApiNetworkException Unknown as Unknown`() {
        val actual = ApiNetworkException(
            kind = ApiResponse.Error.NetworkFailure.Kind.Unknown,
            message = "unexpected",
        ).toSyncError()

        actual.shouldBeInstanceOf<SyncError.Unknown>()
    }

    @Test
    fun `should map AuthenticationException to AuthenticationFailed`() {
        val actual = AuthenticationException("token expired").toSyncError()

        actual.shouldBeInstanceOf<SyncError.Permanent.AuthenticationFailed>()
        actual.message shouldBe "token expired"
    }

    @Test
    fun `should map NoInternetException to NetworkError`() {
        val actual = NoInternetException.toSyncError()

        actual.shouldBeInstanceOf<SyncError.Retryable.NetworkError>()
    }

    @Test
    fun `should unwrap SyncException from cause chain`() {
        val expected = SyncError.Retryable.Timeout("upstream timeout")
        val wrapped = RuntimeException("wrapper", SyncException(expected))

        wrapped.toSyncError() shouldBe expected
    }

    @Test
    fun `should return null for completely unknown exception`() {
        val actual = IllegalStateException("something unexpected").toSyncError()

        actual shouldBe null
    }
}
