package com.thomaskioko.tvmaniac.core.connectivity.implementation

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.thomaskioko.tvmaniac.core.connectivity.api.InternetConnectionChecker
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public actual class PlatformInternetConnectionChecker(
    private val context: Context,
) : InternetConnectionChecker {
    public actual override fun isConnected(): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
                ?: return false
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
            capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }
}
