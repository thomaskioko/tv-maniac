package com.thomaskioko.tvmaniac.util.testing

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
public annotation class FlakyTests(val count: Int = 1)
