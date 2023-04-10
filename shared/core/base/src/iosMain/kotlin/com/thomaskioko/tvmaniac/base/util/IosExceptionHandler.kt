package com.thomaskioko.tvmaniac.base.util

import me.tatarka.inject.annotations.Inject

@Inject
class IosExceptionHandler : ExceptionHandler {

    override fun resolveError(throwable: Throwable): String {
        //TODO:: Implement exception handling
        return "Something went wrong"
    }
}