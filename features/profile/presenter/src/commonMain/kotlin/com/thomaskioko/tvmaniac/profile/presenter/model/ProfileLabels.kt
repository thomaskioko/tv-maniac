package com.thomaskioko.tvmaniac.profile.presenter.model

/**
 * Localized display strings for the profile screen, resolved once in the presenter so both Android
 * and iOS render from the same shared state instead of resolving strings per platform.
 */
public data class ProfileLabels(
    val title: String = "",
    val settingsContentDescription: String = "",
    val profilePictureContentDescription: String = "",
    val editButton: String = "",
    val statsTitle: String = "",
    val episodesWatched: String = "",
    val showsWatched: String = "",
    val watchTime: String = "",
    val monthsShort: String = "",
    val daysShort: String = "",
    val hoursShort: String = "",
    val lists: String = "",
    val viewButton: String = "",
    val unauthenticatedTitle: String = "",
    val footerDescription: String = "",
    val signInButton: String = "",
    val featureDiscoverTitle: String = "",
    val featureDiscoverDescription: String = "",
    val featureTrackTitle: String = "",
    val featureTrackDescription: String = "",
    val featureManageTitle: String = "",
    val featureManageDescription: String = "",
    val featureMoreTitle: String = "",
    val featureMoreDescription: String = "",
)
