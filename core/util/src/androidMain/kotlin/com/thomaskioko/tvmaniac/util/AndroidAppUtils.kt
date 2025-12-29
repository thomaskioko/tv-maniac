package com.thomaskioko.tvmaniac.util

import android.app.Application
import android.content.pm.PackageManager
import com.thomaskioko.tvmaniac.util.api.AppUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class AndroidAppUtils(
    private val context: Application,
) : AppUtils {

    override fun isYoutubePlayerInstalled(): Flow<Boolean> = flow {
        val playerAppInstalled = context.packageManager.getInstalledApplications(PackageManager.GET_META_DATA).firstOrNull {
            it.packageName == "com.google.android.webview"
        } != null

        emit(playerAppInstalled)
    }
}
