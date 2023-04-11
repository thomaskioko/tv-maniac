package com.thomaskioko.tvmaniac.traktauth

import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationResponse

interface TraktAuthManager {
    fun buildLoginActivityResult(): TraktActivityResultContract =
        TraktActivityResultContract { buildLoginIntent() }

    fun buildLoginIntent(): Intent
    fun onLoginResult(result: TraktActivityResultContract.Result)
}

class TraktActivityResultContract internal constructor(
    private val intentBuilder: () -> Intent
) : ActivityResultContract<Unit, TraktActivityResultContract.Result?>() {
    override fun createIntent(context: Context, input: Unit): Intent = intentBuilder()

    override fun parseResult(
        resultCode: Int,
        intent: Intent?
    ): Result? = intent?.let {
        Result(
            AuthorizationResponse.fromIntent(it),
            AuthorizationException.fromIntent(it)
        )
    }

    data class Result(
        val response: AuthorizationResponse?,
        val exception: AuthorizationException?
    )
}
