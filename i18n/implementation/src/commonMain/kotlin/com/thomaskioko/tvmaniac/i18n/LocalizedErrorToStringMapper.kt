package com.thomaskioko.tvmaniac.i18n

import com.thomaskioko.tvmaniac.core.networkutil.api.model.SyncError
import com.thomaskioko.tvmaniac.core.networkutil.api.model.SyncException
import com.thomaskioko.tvmaniac.core.view.ErrorToStringMapper
import com.thomaskioko.tvmaniac.i18n.api.Localizer
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class LocalizedErrorToStringMapper(
    private val localizer: Localizer,
) : ErrorToStringMapper {

    override fun mapError(throwable: Throwable): String {
        val syncException = throwable as? SyncException
            ?: throwable.cause as? SyncException
        return if (syncException != null) {
            mapSyncError(syncException.syncError)
        } else {
            localizer.getString(StringResourceKey.ErrorGeneric)
        }
    }

    private fun mapSyncError(error: SyncError): String = when (error) {
        is SyncError.Retryable.RateLimited -> localizer.getString(StringResourceKey.ErrorRateLimited)
        is SyncError.Retryable.Timeout -> localizer.getString(StringResourceKey.ErrorTimeout)
        is SyncError.Retryable.NetworkError -> localizer.getString(StringResourceKey.ErrorNetwork)
        is SyncError.Retryable.ServerError -> localizer.getString(StringResourceKey.ErrorServer)
        is SyncError.Permanent.AuthenticationFailed -> localizer.getString(StringResourceKey.ErrorAuthFailed)
        is SyncError.Permanent.NotFound -> localizer.getString(StringResourceKey.ErrorNotFound)
        is SyncError.Permanent.InvalidData -> localizer.getString(StringResourceKey.ErrorParse)
        is SyncError.Permanent.Forbidden -> localizer.getString(StringResourceKey.ErrorForbidden)
        is SyncError.Unknown -> localizer.getString(StringResourceKey.ErrorGeneric)
    }
}
