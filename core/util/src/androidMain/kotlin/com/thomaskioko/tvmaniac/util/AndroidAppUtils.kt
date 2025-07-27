package com.thomaskioko.tvmaniac.util

import android.app.Application
import android.content.pm.PackageManager
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
class AndroidAppUtils(
    private val context: Application,
) : AppUtils {

    override fun isYoutubePlayerInstalled(): Flow<Boolean> = flow {
        val playerAppInstalled = context.packageManager.getInstalledApplications(PackageManager.GET_META_DATA).firstOrNull {
            it.packageName == "com.google.android.webview"
        } != null

        emit(playerAppInstalled)
    }
}
