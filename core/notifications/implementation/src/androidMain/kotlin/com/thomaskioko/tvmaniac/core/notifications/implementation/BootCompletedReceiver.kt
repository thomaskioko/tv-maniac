package com.thomaskioko.tvmaniac.core.notifications.implementation

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

public class BootCompletedReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        // We don't need to do anything here, as the AppInitializers should do what we need
    }
}
