package com.thomaskioko.tvmaniac.traktauth.implementation

import android.app.Application
import android.net.Uri
import androidx.core.net.toUri
import com.thomaskioko.tvmaniac.core.base.annotations.ActivityScope
import com.thomaskioko.tvmaniac.core.base.model.Configs
import me.tatarka.inject.annotations.Provides
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationService
import net.openid.appauth.AuthorizationServiceConfiguration
import net.openid.appauth.ClientAuthentication
import net.openid.appauth.ClientSecretBasic
import net.openid.appauth.ResponseTypeValues
import software.amazon.lastmile.kotlin.inject.anvil.ContributesTo
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@ContributesTo(ActivityScope::class)
interface TraktAuthAndroidComponent {

  @Provides
  @SingleIn(ActivityScope::class)
  fun provideAuthConfig(): AuthorizationServiceConfiguration {
    return AuthorizationServiceConfiguration(
      "https://trakt.tv/oauth/authorize".toUri(),
      "https://trakt.tv/oauth/token".toUri(),
    )
  }

  @Provides
  @SingleIn(ActivityScope::class)
  fun provideAuthRequest(
    configuration: AuthorizationServiceConfiguration,
    configs: Configs,
  ): AuthorizationRequest =
    AuthorizationRequest.Builder(
      configuration,
      configs.traktClientId,
      ResponseTypeValues.CODE,
      configs.traktRedirectUri.toUri(),
    )
      .apply { setCodeVerifier(null) }
      .build()

  @Provides
  fun provideClientAuth(configs: Configs): ClientAuthentication =
    ClientSecretBasic(configs.traktClientSecret)

  @Provides
  @SingleIn(ActivityScope::class)
  fun provideAuthorizationService(application: Application): AuthorizationService =
    AuthorizationService(application)

}
