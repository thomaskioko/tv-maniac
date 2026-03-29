package com.thomaskioko.tvmaniac.core.view

public fun interface ErrorToStringMapper {
    public fun mapError(throwable: Throwable): String
}
