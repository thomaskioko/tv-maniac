package com.thomaskioko.tvmaniac.i18n

import android.content.Context
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.desc.desc

public fun StringResource.resolve(context: Context) = desc().toString(context)
