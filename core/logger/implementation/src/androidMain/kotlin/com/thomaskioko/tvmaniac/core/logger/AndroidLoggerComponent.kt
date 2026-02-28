package com.thomaskioko.tvmaniac.core.logger

import android.app.Application
import com.google.firebase.FirebaseApp
import com.google.firebase.crashlytics.FirebaseCrashlytics
import me.tatarka.inject.annotations.Provides
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesTo

@ContributesTo(AppScope::class)
public interface AndroidLoggerComponent {

    @Provides
    public fun provideFirebaseApp(
        application: Application,
    ): FirebaseApp? = FirebaseApp.initializeApp(application)

    @Provides
    public fun provideFirebaseCrashlytics(
        firebaseApp: FirebaseApp?,
    ): FirebaseCrashlytics? = firebaseApp?.let { FirebaseCrashlytics.getInstance() }
}
