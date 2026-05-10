package com.thomaskioko.tvmaniac.presenter.root.model

public data class ToastState(
    val message: String? = null,
    val type: ToastType = ToastType.Status,
    val persistent: Boolean = false,
    val id: Long? = null,
)
