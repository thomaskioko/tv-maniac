package com.thomaskioko.tvmaniac.core.networkutil.model

import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.core.networkutil.api.model.SyncError
import com.thomaskioko.tvmaniac.core.networkutil.api.model.isPermanent
import com.thomaskioko.tvmaniac.core.networkutil.api.model.isRetryable
import com.thomaskioko.tvmaniac.core.networkutil.api.model.toSyncError
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlin.test.Test

internal class SyncErrorTest {

    @Test
    fun `should classify 401 as AuthenticationFailed`() {
        val error = ApiResponse.Error.HttpError<Unit>(401, null, "Unauthorized")

        val result = error.toSyncError()

        result.shouldBeInstanceOf<SyncError.Permanent.AuthenticationFailed>()
        result.message shouldBe "Unauthorized"
    }

    @Test
    fun `should classify 403 as Forbidden`() {
        val error = ApiResponse.Error.HttpError<Unit>(403, null, "Access denied")

        val result = error.toSyncError()

        result.shouldBeInstanceOf<SyncError.Permanent.Forbidden>()
        result.message shouldBe "Access denied"
    }

    @Test
    fun `should classify 404 as NotFound`() {
        val error = ApiResponse.Error.HttpError<Unit>(404, null, "Not found")

        val result = error.toSyncError()

        result.shouldBeInstanceOf<SyncError.Permanent.NotFound>()
        result.message shouldBe "Not found"
    }

    @Test
    fun `should classify 408 as Timeout`() {
        val error = ApiResponse.Error.HttpError<Unit>(408, null, "Request timeout")

        val result = error.toSyncError()

        result.shouldBeInstanceOf<SyncError.Retryable.Timeout>()
        result.message shouldBe "Request timeout"
    }

    @Test
    fun `should classify 429 as RateLimited`() {
        val error = ApiResponse.Error.HttpError<Unit>(429, null, "Rate limit exceeded")

        val result = error.toSyncError()

        result.shouldBeInstanceOf<SyncError.Retryable.RateLimited>()
        result.message shouldBe "Rate limit exceeded"
    }

    @Test
    fun `should classify 500 as ServerError`() {
        val error = ApiResponse.Error.HttpError<Unit>(500, null, "Internal server error")

        val result = error.toSyncError()

        val serverError = result.shouldBeInstanceOf<SyncError.Retryable.ServerError>()
        serverError.message shouldBe "Internal server error"
        serverError.statusCode shouldBe 500
    }

    @Test
    fun `should classify 502 as ServerError`() {
        val error = ApiResponse.Error.HttpError<Unit>(502, null, "Bad gateway")

        val result = error.toSyncError()

        result.shouldBeInstanceOf<SyncError.Retryable.ServerError>()
    }

    @Test
    fun `should classify 503 as ServerError`() {
        val error = ApiResponse.Error.HttpError<Unit>(503, null, "Service unavailable")

        val result = error.toSyncError()

        result.shouldBeInstanceOf<SyncError.Retryable.ServerError>()
    }

    @Test
    fun `should classify unknown HTTP code as Unknown`() {
        val error = ApiResponse.Error.HttpError<Unit>(418, null, "I'm a teapot")

        val result = error.toSyncError()

        result.shouldBeInstanceOf<SyncError.Unknown>()
        result.message shouldBe "I'm a teapot"
    }

    @Test
    fun `should use default message given null error message`() {
        val error = ApiResponse.Error.HttpError<Unit>(500, null, null)

        val result = error.toSyncError()

        result.shouldBeInstanceOf<SyncError.Retryable.ServerError>()
        result.message shouldBe "HTTP Error 500"
    }

    @Test
    fun `should classify SerializationError as InvalidData`() {
        val error = ApiResponse.Error.SerializationError("Parse failed", "Invalid JSON")

        val result = error.toSyncError()

        result.shouldBeInstanceOf<SyncError.Permanent.InvalidData>()
        result.message shouldBe "Parse failed"
    }

    @Test
    fun `should use default message given SerializationError with null message`() {
        val error = ApiResponse.Error.SerializationError(null, null)

        val result = error.toSyncError()

        result.shouldBeInstanceOf<SyncError.Permanent.InvalidData>()
        result.message shouldBe "Failed to parse response data."
    }

    @Test
    fun `should classify GenericError with timeout keyword as Timeout`() {
        val error = ApiResponse.Error.GenericError("Connection timeout", null)

        val result = error.toSyncError()

        result.shouldBeInstanceOf<SyncError.Retryable.Timeout>()
    }

    @Test
    fun `should classify GenericError with timed out keyword as Timeout`() {
        val error = ApiResponse.Error.GenericError("Request timed out", null)

        val result = error.toSyncError()

        result.shouldBeInstanceOf<SyncError.Retryable.Timeout>()
    }

    @Test
    fun `should classify GenericError with network keyword as NetworkError`() {
        val error = ApiResponse.Error.GenericError("Network unavailable", null)

        val result = error.toSyncError()

        result.shouldBeInstanceOf<SyncError.Retryable.NetworkError>()
    }

    @Test
    fun `should classify GenericError with connection keyword as NetworkError`() {
        val error = ApiResponse.Error.GenericError("Connection refused", null)

        val result = error.toSyncError()

        result.shouldBeInstanceOf<SyncError.Retryable.NetworkError>()
    }

    @Test
    fun `should classify GenericError with socket keyword as NetworkError`() {
        val error = ApiResponse.Error.GenericError("Socket closed", null)

        val result = error.toSyncError()

        result.shouldBeInstanceOf<SyncError.Retryable.NetworkError>()
    }

    @Test
    fun `should classify GenericError with dns keyword as NetworkError`() {
        val error = ApiResponse.Error.GenericError("DNS resolution failed", null)

        val result = error.toSyncError()

        result.shouldBeInstanceOf<SyncError.Retryable.NetworkError>()
    }

    @Test
    fun `should classify GenericError without keywords as Unknown`() {
        val error = ApiResponse.Error.GenericError("Something went wrong", null)

        val result = error.toSyncError()

        result.shouldBeInstanceOf<SyncError.Unknown>()
    }

    @Test
    fun `should return true given isRetryable on retryable error`() {
        val error: SyncError = SyncError.Retryable.RateLimited()

        error.isRetryable shouldBe true
        error.isPermanent shouldBe false
    }

    @Test
    fun `should return true given isPermanent on permanent error`() {
        val error: SyncError = SyncError.Permanent.AuthenticationFailed()

        error.isPermanent shouldBe true
        error.isRetryable shouldBe false
    }

    @Test
    fun `should return false given isRetryable on unknown error`() {
        val error: SyncError = SyncError.Unknown("Unknown")

        error.isRetryable shouldBe false
        error.isPermanent shouldBe false
    }
}
