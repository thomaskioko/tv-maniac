package com.thomaskioko.tvmaniac.i18n

import androidx.compose.runtime.Composable
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc

@Composable
fun StringResource.resolve(): String = desc().localized()
