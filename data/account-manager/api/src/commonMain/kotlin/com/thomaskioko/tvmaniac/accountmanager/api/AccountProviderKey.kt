package com.thomaskioko.tvmaniac.accountmanager.api

import dev.zacsweers.metro.MapKey

@MapKey
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.TYPE)
public annotation class AccountProviderKey(val value: SyncProviderSource)
