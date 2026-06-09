package com.thomaskioko.tvmaniac.oauth.implementation

import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationResponse
import net.openid.appauth.AuthorizationService

internal class OAuthActivityResultContract(
    private val authService: AuthorizationService,
) : ActivityResultContract<AuthorizationRequest, OAuthActivityResultContract.Result?>() {

    override fun createIntent(context: Context, input: AuthorizationRequest): Intent =
        authService.getAuthorizationRequestIntent(input)

    override fun parseResult(resultCode: Int, intent: Intent?): Result? =
        intent?.let {
            Result(
                response = AuthorizationResponse.fromIntent(it),
                exception = AuthorizationException.fromIntent(it),
            )
        }

    data class Result(
        val response: AuthorizationResponse?,
        val exception: AuthorizationException?,
    )
}
