package com.thomaskioko.tvmaniac.common.voyagerutil.inject

import android.annotation.SuppressLint
import androidx.compose.runtime.ProvidedValue
import androidx.compose.runtime.compositionLocalOf
import com.thomaskioko.tvmaniac.common.voyagerutil.ScreenModelComponent
import me.tatarka.inject.annotations.Provides

interface VoyagerUiComponent {
    val hooks: Array<ProvidedValue<out Any?>>

    @Provides
    fun provideProvidedValues(
        screenModelComponent: ScreenModelComponent,
    ): Array<ProvidedValue<out Any?>> = arrayOf(
        LocalScreenModels provides screenModelComponent,
    )
}

@SuppressLint("ComposeCompositionLocalUsage")
val LocalScreenModels = compositionLocalOf<ScreenModelComponent> {
    throw IllegalArgumentException("ScreenModelComponent not found")
}
