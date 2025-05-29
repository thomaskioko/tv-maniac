package com.thomaskioko.tvmaniac.i18n

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.thomaskioko.tvmaniac.i18n.api.Localizer
import org.junit.Before
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [33])
internal class MokoLocalizerAndroidTest : MokoLocalizerTest() {
    override lateinit var localizer: Localizer

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        localizer = MokoResourcesLocalizer(PlatformLocalizer(context))
    }
}
