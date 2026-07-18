package com.thomaskioko.tvmaniac.util.testing

/**
 * Marks a load-sensitive test that retries up to [count] attempts and passes on the first success.
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
public annotation class FlakyTests(val count: Int = 1)
