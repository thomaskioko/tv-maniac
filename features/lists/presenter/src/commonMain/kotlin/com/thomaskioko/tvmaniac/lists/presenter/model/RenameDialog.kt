package com.thomaskioko.tvmaniac.lists.presenter.model

public data class RenameDialog(
    val title: String,
    val name: String,
    val canSave: Boolean,
    val saveLabel: String,
    val isSaving: Boolean = false,
)
