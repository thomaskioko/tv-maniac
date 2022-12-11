package com.thomaskioko.tvmaniac.core.util

import kotlinx.coroutines.flow.Flow

 expect class AppUtils actual constructor(context: AppContext){

    fun isYoutubePlayerInstalled(): Flow<Boolean>
}