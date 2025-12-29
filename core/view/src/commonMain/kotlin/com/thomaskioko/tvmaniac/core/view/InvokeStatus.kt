package com.thomaskioko.tvmaniac.core.view

public sealed class InvokeStatus
public object InvokeStarted : InvokeStatus()
public object InvokeSuccess : InvokeStatus()
public data class InvokeError(val throwable: Throwable) : InvokeStatus()
