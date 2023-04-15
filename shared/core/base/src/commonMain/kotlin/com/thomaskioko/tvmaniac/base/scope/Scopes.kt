package com.thomaskioko.tvmaniac.base.scope

import me.tatarka.inject.annotations.Scope
import kotlin.annotation.AnnotationTarget.CLASS
import kotlin.annotation.AnnotationTarget.FUNCTION
import kotlin.annotation.AnnotationTarget.PROPERTY_GETTER

@Scope
@Target(CLASS, FUNCTION, PROPERTY_GETTER)
annotation class ApplicationScope

@Scope
annotation class ActivityScope

@Scope
@Target(CLASS, FUNCTION, PROPERTY_GETTER)
annotation class Singleton
