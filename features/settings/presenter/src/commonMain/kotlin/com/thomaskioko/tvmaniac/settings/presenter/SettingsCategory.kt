package com.thomaskioko.tvmaniac.settings.presenter

import kotlinx.collections.immutable.ImmutableList

public data class SettingsCategoryItem(
    val page: SettingsPage,
    val title: String,
    val description: String,
)

public data class SettingsCategoryGroup(
    val label: String,
    val items: ImmutableList<SettingsCategoryItem>,
)
