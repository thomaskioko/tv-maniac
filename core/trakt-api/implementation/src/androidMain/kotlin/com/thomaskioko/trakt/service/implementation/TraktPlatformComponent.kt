package com.thomaskioko.trakt.service.implementation

import com.thomaskioko.tvmaniac.util.scope.ApplicationScope
import io.ktor.client.engine.okhttp.OkHttp
import me.tatarka.inject.annotations.Provides

actual interface TraktPlatformComponent {

    @ApplicationScope
    @Provides
    fun provideTraktHttpClientEngine(
        interceptor: TraktAuthInterceptor,
    ): TraktHttpClientEngine = OkHttp.create {
        addInterceptor(interceptor)
    }
}
