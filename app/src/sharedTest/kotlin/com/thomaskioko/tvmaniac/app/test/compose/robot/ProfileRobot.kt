package com.thomaskioko.tvmaniac.app.test.compose.robot

import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import com.thomaskioko.tvmaniac.testing.integration.ui.BaseRobot
import com.thomaskioko.tvmaniac.testtags.component.CollapsibleSectionTestTags
import com.thomaskioko.tvmaniac.testtags.profile.ProfileTestTags

@OptIn(ExperimentalTestApi::class)
internal class ProfileRobot(composeUi: ComposeUiTest) : BaseRobot<ProfileRobot>(composeUi) {

    fun assertProfileScreenDisplayed() = apply {
        assertDisplayed(ProfileTestTags.SCREEN_TEST_TAG)
    }

    fun assertSignInButtonDisplayed() = apply {
        scrollTo(ProfileTestTags.SIGN_IN_BUTTON_TEST_TAG)
        assertDisplayed(ProfileTestTags.SIGN_IN_BUTTON_TEST_TAG)
    }

    fun assertUserCardDisplayed(slug: String) = apply {
        assertDisplayed(ProfileTestTags.userCard(slug))
    }

    fun assertUserNameDisplayed() = apply {
        assertDisplayed(ProfileTestTags.USERNAME_TEST_TAG)
    }

    fun scrollToUserLists(slug: String) = apply {
        scrollToListTag(
            listTag = ProfileTestTags.userCard(slug),
            itemTag = ProfileTestTags.USER_LISTS_ROW_TEST_TAG,
        )
    }

    fun assertUserListsRowDisplayed() = apply {
        assertDisplayed(ProfileTestTags.USER_LISTS_ROW_TEST_TAG)
    }

    fun assertListCardDisplayed(id: Long) = apply {
        assertDisplayed(ProfileTestTags.listCard(id))
    }

    fun assertListCardExists(id: Long) = apply {
        assertExists(ProfileTestTags.listCard(id))
    }

    fun clickUserListsToggle() = apply {
        click(CollapsibleSectionTestTags.toggle(ProfileTestTags.USER_LISTS_SECTION_KEY))
    }

    fun assertUserListsRowDoesNotExist() = apply {
        assertDoesNotExist(ProfileTestTags.USER_LISTS_ROW_TEST_TAG)
    }

    fun clickSignInButton() = apply {
        scrollTo(ProfileTestTags.SIGN_IN_BUTTON_TEST_TAG)
        click(ProfileTestTags.SIGN_IN_BUTTON_TEST_TAG)
    }

    fun assertStatsCardDisplayed() = apply {
        assertTextDisplayed(ProfileTestTags.STATS_SECTION_TITLE)
    }

    fun scrollToRecentlyWatched(slug: String) = apply {
        scrollToListTag(
            listTag = ProfileTestTags.userCard(slug),
            itemTag = ProfileTestTags.RECENTLY_WATCHED_ROW_TEST_TAG,
        )
    }

    fun assertRecentlyWatchedSectionDisplayed() = apply {
        assertTextDisplayed(text = "Recently Watched", timeoutMillis = 10_000L)
    }

    fun scrollToProgressSection(slug: String) = apply {
        scrollToListTag(
            listTag = ProfileTestTags.userCard(slug),
            itemTag = ProfileTestTags.PROGRESS_IN_PROGRESS_CHIP_TEST_TAG,
        )
    }

    fun selectInProgressFilter() = apply {
        click(ProfileTestTags.PROGRESS_IN_PROGRESS_CHIP_TEST_TAG)
    }

    fun assertInProgressShowDisplayed() = apply {
        assertDisplayed(ProfileTestTags.PROGRESS_ROW_TEST_TAG)
    }

    fun clickSettingsButton() = apply {
        click(ProfileTestTags.SETTINGS_BUTTON_TEST_TAG)
    }
}
