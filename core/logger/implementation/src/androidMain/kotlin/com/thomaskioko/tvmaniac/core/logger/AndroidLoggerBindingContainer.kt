package com.thomaskioko.tvmaniac.core.logger

import android.app.Application
import com.google.firebase.FirebaseApp
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides

@BindingContainer
@ContributesTo(AppScope::class)
public object AndroidLoggerBindingContainer {

    @Provides
    public fun provideFirebaseApp(
        application: Application,
    ): FirebaseApp? = FirebaseApp.initializeApp(application)

    @Provides
    public fun provideFirebaseCrashlytics(
        firebaseApp: FirebaseApp?,
    ): FirebaseCrashlytics? = firebaseApp?.let { FirebaseCrashlytics.getInstance() }
}
