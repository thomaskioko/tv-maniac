package com.thomaskioko.tvmaniac.core.util

import android.content.pm.PackageManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow


actual class AppUtils actual constructor(private val context: AppContext) {

    actual fun isYoutubePlayerInstalled(): Flow<Boolean> = flow {

        val playerAppInstalled = context.packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
            .firstOrNull { it.packageName == "com.google.android.webview" } != null

        emit(playerAppInstalled)
    }
}
