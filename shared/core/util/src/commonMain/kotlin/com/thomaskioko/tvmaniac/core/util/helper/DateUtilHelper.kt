package com.thomaskioko.tvmaniac.core.util.helper

import com.thomaskioko.tvmaniac.core.util.DateUtil

interface DateUtilHelper {
    fun getTimestampMilliseconds(): Long
}

internal class DateUtilHelperImpl : DateUtilHelper {
    override fun getTimestampMilliseconds(): Long = DateUtil.getTimestampMilliseconds()
}