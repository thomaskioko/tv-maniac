package com.thomaskioko.tvmaniac.traktauth.implementation

import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import com.thomaskioko.tvmaniac.core.base.annotations.ActivityScope
import com.thomaskioko.tvmaniac.core.logger.KermitLogger
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthManager
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthRepository
import me.tatarka.inject.annotations.Inject
import net.openid.appauth.AuthState
import net.openid.appauth.AuthorizationService
import net.openid.appauth.ClientAuthentication
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@Inject
@SingleIn(ActivityScope::class)
@ContributesBinding(ActivityScope::class)
class DefaultTraktAuthManager(
  private val activity: ComponentActivity,
  private val traktActivityResultContract: TraktActivityResultContract,
  private val traktAuthRepository: TraktAuthRepository,
  private val clientAuth: Lazy<ClientAuthentication>,
  private val authService: Lazy<AuthorizationService>,
  private val logger: KermitLogger,
) : TraktAuthManager {

  private lateinit var launcher: ActivityResultLauncher<Unit>

  override fun registerResult() {
    launcher =
      activity.registerForActivityResult(traktActivityResultContract) { result ->
        if (result != null) {
          onLoginResult(result)
        }
      }
  }

  override fun launchWebView() = launcher.launch(Unit)

  private fun onLoginResult(result: TraktActivityResultContract.Result) {
    val (response, error) = result
    when {
      response != null -> {
        authService.value.performTokenRequest(
          response.createTokenExchangeRequest(),
          clientAuth.value,
        ) { tokenResponse, ex ->
          val state = AuthState().apply { update(tokenResponse, ex) }
          traktAuthRepository.onNewAuthState(
            com.thomaskioko.tvmaniac.datastore.api.AuthState(
              accessToken = state.accessToken,
              refreshToken = state.refreshToken,
              isAuthorized = state.isAuthorized,
            ),
          )
        }
      }
      error != null -> logger.error("AuthException", error)
    }
  }
}
