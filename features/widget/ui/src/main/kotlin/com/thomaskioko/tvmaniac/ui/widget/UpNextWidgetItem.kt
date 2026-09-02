package com.thomaskioko.tvmaniac.ui.widget

import android.graphics.Bitmap

internal data class UpNextWidgetItem(
    val showId: Long,
    val showName: String,
    val episodeName: String,
    val seasonEpisodeLabel: String,
    val poster: Bitmap?,
    val url: String?,
)
