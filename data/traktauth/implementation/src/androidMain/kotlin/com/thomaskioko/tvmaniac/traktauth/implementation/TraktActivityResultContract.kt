package com.thomaskioko.tvmaniac.traktauth.implementation

import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import me.tatarka.inject.annotations.Inject
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationResponse
import net.openid.appauth.AuthorizationService

@Inject
public class TraktActivityResultContract(
    private val authService: AuthorizationService,
    private val request: AuthorizationRequest,
) : ActivityResultContract<Unit, TraktActivityResultContract.Result?>() {
    override fun createIntent(context: Context, input: Unit): Intent {
        return authService.getAuthorizationRequestIntent(request)
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Result? =
        intent?.let {
            Result(
                response = AuthorizationResponse.fromIntent(it),
                exception = AuthorizationException.fromIntent(it),
            )
        }

    public data class Result(
        val response: AuthorizationResponse?,
        val exception: AuthorizationException?,
    )
}
