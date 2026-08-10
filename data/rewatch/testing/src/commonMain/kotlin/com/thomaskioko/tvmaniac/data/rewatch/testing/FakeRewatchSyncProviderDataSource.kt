package com.thomaskioko.tvmaniac.data.rewatch.testing

import com.thomaskioko.tvmaniac.accountmanager.api.SyncProviderSource
import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.data.rewatch.api.RemoteRewatchSession
import com.thomaskioko.tvmaniac.data.rewatch.api.RemoteRewatchWrite
import com.thomaskioko.tvmaniac.data.rewatch.api.RemoteRewatchWriteResult
import com.thomaskioko.tvmaniac.data.rewatch.api.RewatchSyncProviderDataSource

public class FakeRewatchSyncProviderDataSource(
    override val provider: SyncProviderSource = SyncProviderSource.TRAKT,
) : RewatchSyncProviderDataSource {

    private var supportsRewatchValue: Boolean = true
    private var readResponse: ApiResponse<List<RemoteRewatchSession>> = ApiResponse.Success(emptyList())
    private var writeResponse: ApiResponse<RemoteRewatchWriteResult> = ApiResponse.Success(RemoteRewatchWriteResult())
    private var writeHandler: ((RemoteRewatchWrite) -> ApiResponse<RemoteRewatchWriteResult>)? = null
    private var readException: Throwable? = null
    private var writeException: Throwable? = null

    public var lastWrite: RemoteRewatchWrite? = null
        private set

    public fun setSupportsRewatch(value: Boolean) {
        supportsRewatchValue = value
    }

    public fun setReadRewatchSessionsResponse(response: ApiResponse<List<RemoteRewatchSession>>) {
        readResponse = response
    }

    public fun setWriteRewatchResponse(response: ApiResponse<RemoteRewatchWriteResult>) {
        writeResponse = response
    }

    public fun setWriteRewatchHandler(handler: (RemoteRewatchWrite) -> ApiResponse<RemoteRewatchWriteResult>) {
        writeHandler = handler
    }

    public fun setReadRewatchSessionsException(exception: Throwable) {
        readException = exception
    }

    public fun setWriteRewatchException(exception: Throwable) {
        writeException = exception
    }

    override suspend fun supportsRewatch(): Boolean = supportsRewatchValue

    override suspend fun readRewatchSessions(providerShowId: Long): ApiResponse<List<RemoteRewatchSession>> {
        readException?.let { throw it }
        return readResponse
    }

    override suspend fun writeRewatch(write: RemoteRewatchWrite): ApiResponse<RemoteRewatchWriteResult> {
        lastWrite = write
        writeException?.let { throw it }
        return writeHandler?.invoke(write) ?: writeResponse
    }
}
