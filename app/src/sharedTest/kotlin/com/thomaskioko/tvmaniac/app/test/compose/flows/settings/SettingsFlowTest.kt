package com.thomaskioko.tvmaniac.app.test.compose.flows.settings

import com.thomaskioko.tvmaniac.app.test.BaseAppFlowTest
import com.thomaskioko.tvmaniac.datastore.api.AutoBackupInterval
import com.thomaskioko.tvmaniac.datastore.api.ImageQuality
import com.thomaskioko.tvmaniac.settings.presenter.ThemeModel
import com.thomaskioko.tvmaniac.subscription.api.AccountType
import com.thomaskioko.tvmaniac.testtags.home.HomeTestTags
import com.thomaskioko.tvmaniac.testtags.settings.SettingsTestTags
import org.junit.Test

internal class SettingsFlowTest : BaseAppFlowTest() {

    @Test
    fun givenSettings_whenLayoutRowClicked_thenPageOpensAndBackReturnsToRoot() = runAppFlowTest {
        scenarios.discover.stubBrowseGraph()

        discoverRobot
            .assertDiscoverScreenDisplayed()

        scenarios.stubUsersMeUnauthorized()

        homeRobot
            .clickProfileTab()
            .assertTabSelected(HomeTestTags.PROFILE_TAB)

        profileRobot
            .assertSignInButtonDisplayed()
            .clickSettingsButton()

        settingsRobot
            .assertSettingsScreenDisplayed()
            .openLayoutPage()
            .assertDoesNotExist(SettingsTestTags.GENERAL_LAYOUT_ROW_TEST_TAG)
            .clickBackButton()
            .assertDisplayed(SettingsTestTags.GENERAL_LAYOUT_ROW_TEST_TAG)
    }

    @Test
    fun givenSettings_whenImageQualitySelected_thenSelectionIsPersisted() = runAppFlowTest {
        scenarios.discover.stubBrowseGraph()

        discoverRobot
            .assertDiscoverScreenDisplayed()

        scenarios.stubUsersMeUnauthorized()

        homeRobot
            .clickProfileTab()
            .assertTabSelected(HomeTestTags.PROFILE_TAB)

        profileRobot
            .assertSignInButtonDisplayed()
            .clickSettingsButton()

        settingsRobot
            .assertSettingsScreenDisplayed()
            .assertDoesNotExist(HomeTestTags.NAVIGATION_BAR)
            .openAppearancePage()
            .scrollToImageQualityChip(ImageQuality.HIGH)
            .assertImageQualitySelected(ImageQuality.AUTO)
            .assertImageQualityNotSelected(ImageQuality.HIGH)
            .clickImageQualityChip(ImageQuality.HIGH)
            .assertImageQualitySelected(ImageQuality.HIGH)
            .assertImageQualityNotSelected(ImageQuality.AUTO)
    }

    @Test
    fun givenSettings_whenWidgetThemeSelected_thenSelectionIsPersisted() = runAppFlowTest {
        scenarios.discover.stubBrowseGraph()

        discoverRobot
            .assertDiscoverScreenDisplayed()

        scenarios.stubUsersMeUnauthorized()

        homeRobot
            .clickProfileTab()
            .assertTabSelected(HomeTestTags.PROFILE_TAB)

        profileRobot
            .assertSignInButtonDisplayed()
            .clickSettingsButton()

        settingsRobot
            .assertSettingsScreenDisplayed()
            .openWidgetAppearancePage()
            .assertWidgetThemeSelected(null)
            .assertWidgetThemeNotSelected(ThemeModel.CRIMSON)
            .clickWidgetThemeChip(ThemeModel.CRIMSON)
            .assertWidgetThemeSelected(ThemeModel.CRIMSON)
            .assertWidgetThemeNotSelected(null)
    }

    @Test
    fun givenAuthenticatedUser_whenTraktAccountClicked_thenShowsLogoutDialog() = runAppFlowTest {
        scenarios.discover.stubBrowseGraph()

        discoverRobot
            .assertDiscoverScreenDisplayed()

        scenarios.signInAndDismissRationale()

        homeRobot
            .clickProfileTab()
            .assertTabSelected(HomeTestTags.PROFILE_TAB)

        profileRobot
            .assertUserCardDisplayed("integration-test-user")
            .clickSettingsButton()

        settingsRobot
            .assertSettingsScreenDisplayed()
            .openTraktPage()
            .scrollToTraktAccountRow()
            .clickTraktAccountRow()
            .assertLogoutDialogDisplayed()
            .clickLogoutDismiss()
            .assertLogoutDialogDoesNotExist()
    }

    @Test
    fun givenAuthenticatedUser_whenLogoutConfirmed_thenSignsOut() = runAppFlowTest {
        scenarios.discover.stubBrowseGraph()

        discoverRobot
            .assertDiscoverScreenDisplayed()

        scenarios.signInAndDismissRationale()

        homeRobot
            .clickProfileTab()
            .assertTabSelected(HomeTestTags.PROFILE_TAB)

        profileRobot
            .assertUserCardDisplayed("integration-test-user")
            .clickSettingsButton()

        settingsRobot
            .assertSettingsScreenDisplayed()
            .openTraktPage()
            .scrollToTraktAccountRow()
            .clickTraktAccountRow()
            .assertLogoutDialogDisplayed()
            .clickLogoutConfirm()
            .clickBackButton()
            .clickBackButton()

        profileRobot
            .assertSignInButtonDisplayed()
    }

    @Test
    fun givenDebugMenu_whenAccountTypeSelected_thenRowSubtitleUpdates() = runAppFlowTest {
        scenarios.discover.stubBrowseGraph()

        discoverRobot
            .assertDiscoverScreenDisplayed()

        scenarios.stubUsersMeUnauthorized()

        homeRobot
            .clickProfileTab()
            .assertTabSelected(HomeTestTags.PROFILE_TAB)

        profileRobot
            .assertSignInButtonDisplayed()
            .clickSettingsButton()

        settingsRobot
            .assertSettingsScreenDisplayed()
            .openDebugMenu()

        debugRobot
            .assertDebugMenuScreenDisplayed()
            .scrollToAccountTypeRow()
            .assertAccountTypeRowSubtitle("Update account type")
            .clickAccountTypeRow()
            .assertAccountTypeDialogDisplayed()
            .assertAccountTypeOptionNotSelected(AccountType.Free)
            .assertAccountTypeOptionNotSelected(AccountType.Premium)
            .selectAccountType(AccountType.Premium)
            .assertAccountTypeRowSubtitle("Premium")
    }

    @Test
    fun givenPaywallEnabled_whenAccountTypeChanges_thenLockedSurfacesUpdate() = runAppFlowTest {
        scenarios.discover.stubBrowseGraph()
        scenarios.flags.enablePaywall()

        discoverRobot
            .assertDiscoverScreenDisplayed()

        scenarios.stubUsersMeUnauthorized()

        homeRobot
            .clickProfileTab()
            .assertTabSelected(HomeTestTags.PROFILE_TAB)

        profileRobot
            .assertSignInButtonDisplayed()
            .clickSettingsButton()

        settingsRobot
            .assertSettingsScreenDisplayed()
            .openDebugMenu()

        debugRobot
            .assertDebugMenuScreenDisplayed()
            .scrollToAccountTypeRow()
            .clickAccountTypeRow()
            .assertAccountTypeDialogDisplayed()
            .selectAccountType(AccountType.Free)
            .pressBack()

        settingsRobot
            .assertSettingsScreenDisplayed()
            .clickBackButton()
            .openAppearancePage()
            .scrollToThemesLocked()
            .assertThemeSwatchDoesNotExist(ThemeModel.TERMINAL)
            .clickBackButton()
            .openBackupPage()
            .assertBackupLockedDisplayed()
            .assertBackupExportRowDoesNotExist()
            .assertBackupImportRowDoesNotExist()
            .clickBackButton()
            .clickBackButton()

        homeRobot
            .clickProgressTab()
            .assertTabSelected(HomeTestTags.PROGRESS_TAB)

        progressRobot
            .assertProgressScreenDisplayed()
            .clickCalendarTab()
            .assertCalendarTabSelected()

        calendarRobot
            .assertLockedStateDisplayed()

        homeRobot
            .clickProfileTab()
            .assertTabSelected(HomeTestTags.PROFILE_TAB)

        profileRobot
            .assertSignInButtonDisplayed()
            .clickSettingsButton()

        settingsRobot
            .assertSettingsScreenDisplayed()
            .openDebugMenu()

        debugRobot
            .assertDebugMenuScreenDisplayed()
            .scrollToAccountTypeRow()
            .clickAccountTypeRow()
            .assertAccountTypeDialogDisplayed()
            .selectAccountType(AccountType.Premium)
            .pressBack()

        settingsRobot
            .assertSettingsScreenDisplayed()
            .clickBackButton()
            .openAppearancePage()
            .scrollToThemeSwatch(ThemeModel.TERMINAL)
            .clickThemeSwatch(ThemeModel.TERMINAL)
            .assertThemeSwatchSelected(ThemeModel.TERMINAL)
            .clickBackButton()
            .openBackupPage()
            .assertBackupExportRowDisplayed()
            .assertBackupImportRowDisplayed()
            .clickBackButton()
            .clickBackButton()

        homeRobot
            .clickProgressTab()
            .assertTabSelected(HomeTestTags.PROGRESS_TAB)

        progressRobot
            .assertProgressScreenDisplayed()
            .clickCalendarTab()
            .assertCalendarTabSelected()

        calendarRobot
            .assertLoggedOutStateDisplayed()
    }

    @Test
    fun givenSettings_whenBackupRowClicked_thenPageOpensAndBackReturnsToRoot() = runAppFlowTest {
        scenarios.discover.stubBrowseGraph()

        discoverRobot
            .assertDiscoverScreenDisplayed()

        scenarios.stubUsersMeUnauthorized()

        homeRobot
            .clickProfileTab()
            .assertTabSelected(HomeTestTags.PROFILE_TAB)

        profileRobot
            .assertSignInButtonDisplayed()
            .clickSettingsButton()

        settingsRobot
            .assertSettingsScreenDisplayed()
            .openBackupPage()
            .assertBackupExportRowDisplayed()
            .clickBackButton()
            .assertSettingsScreenDisplayed()
    }

    @Test
    fun givenBackupPage_whenImportCancelled_thenNothingChanges() = runAppFlowTest {
        scenarios.discover.stubBrowseGraph()

        discoverRobot
            .assertDiscoverScreenDisplayed()

        scenarios.stubUsersMeUnauthorized()

        homeRobot
            .clickProfileTab()
            .assertTabSelected(HomeTestTags.PROFILE_TAB)

        profileRobot
            .assertSignInButtonDisplayed()
            .clickSettingsButton()

        settingsRobot
            .assertSettingsScreenDisplayed()
            .openBackupPage()
            .assertBackupExportRowDisplayed()
            .assertBackupImportRowDisplayed()
            .clickBackupImportRow()
            .assertBackupRestoreConfirmDisplayed()
            .clickBackupRestoreDismiss()
            .assertBackupRestoreConfirmDoesNotExist()
            .assertBackupExportRowDisplayed()
            .assertBackupImportRowDisplayed()
    }

    @Test
    fun givenBackupPage_whenImportConfirmed_thenConfirmDialogDismisses() = runAppFlowTest {
        scenarios.discover.stubBrowseGraph()

        discoverRobot
            .assertDiscoverScreenDisplayed()

        scenarios.stubUsersMeUnauthorized()

        homeRobot
            .clickProfileTab()
            .assertTabSelected(HomeTestTags.PROFILE_TAB)

        profileRobot
            .assertSignInButtonDisplayed()
            .clickSettingsButton()

        settingsRobot
            .assertSettingsScreenDisplayed()
            .openBackupPage()
            .clickBackupImportRow()
            .assertBackupRestoreConfirmDisplayed()
            .clickBackupRestoreConfirm()
            .assertBackupRestoreConfirmDoesNotExist()
    }

    @Test
    fun givenBackupPage_whenAutomaticBackupTurnedOn_thenScheduleAndLocationAppear() = runAppFlowTest {
        scenarios.discover.stubBrowseGraph()

        discoverRobot
            .assertDiscoverScreenDisplayed()

        scenarios.stubUsersMeUnauthorized()

        homeRobot
            .clickProfileTab()
            .assertTabSelected(HomeTestTags.PROFILE_TAB)

        profileRobot
            .assertSignInButtonDisplayed()
            .clickSettingsButton()

        settingsRobot
            .assertSettingsScreenDisplayed()
            .openBackupPage()
            .assertAutoBackupLocationRowDisplayed()
            .scrollToAutoBackupToggle()
            .assertAutoBackupDisabled()
            .assertAutoBackupNowRowDoesNotExist()
            .clickAutoBackupToggle()
            .assertAutoBackupEnabled()
            .assertAutoBackupNowRowDisplayed()
    }

    @Test
    fun givenAutomaticBackupOn_whenScheduleSelected_thenSelectionIsPersisted() = runAppFlowTest {
        scenarios.discover.stubBrowseGraph()

        discoverRobot
            .assertDiscoverScreenDisplayed()

        scenarios.stubUsersMeUnauthorized()

        homeRobot
            .clickProfileTab()
            .assertTabSelected(HomeTestTags.PROFILE_TAB)

        profileRobot
            .assertSignInButtonDisplayed()
            .clickSettingsButton()

        settingsRobot
            .assertSettingsScreenDisplayed()
            .openBackupPage()
            .scrollToAutoBackupToggle()
            .clickAutoBackupToggle()
            .scrollToAutoBackupScheduleChip(AutoBackupInterval.MONTHLY)
            .assertAutoBackupScheduleSelected(AutoBackupInterval.WEEKLY)
            .clickAutoBackupScheduleChip(AutoBackupInterval.MONTHLY)
            .assertAutoBackupScheduleSelected(AutoBackupInterval.MONTHLY)
            .assertAutoBackupScheduleNotSelected(AutoBackupInterval.WEEKLY)
    }

    @Test
    fun givenBackupPage_whenFileNameChanged_thenSelectionIsPersisted() = runAppFlowTest {
        scenarios.discover.stubBrowseGraph()

        discoverRobot
            .assertDiscoverScreenDisplayed()

        scenarios.stubUsersMeUnauthorized()

        homeRobot
            .clickProfileTab()
            .assertTabSelected(HomeTestTags.PROFILE_TAB)

        profileRobot
            .assertSignInButtonDisplayed()
            .clickSettingsButton()

        settingsRobot
            .assertSettingsScreenDisplayed()
            .openBackupPage()
            .assertBackupFileName("tvmaniac-backup.json")
            .clickBackupFileNameRow()
            .replaceBackupFileName("my shows")
            .clickBackupFileNameSave()
            .assertBackupFileName("my shows.json")
    }

    @Test
    fun givenBackupFileNameDialog_whenCancelled_thenNameIsUnchanged() = runAppFlowTest {
        scenarios.discover.stubBrowseGraph()

        discoverRobot
            .assertDiscoverScreenDisplayed()

        scenarios.stubUsersMeUnauthorized()

        homeRobot
            .clickProfileTab()
            .assertTabSelected(HomeTestTags.PROFILE_TAB)

        profileRobot
            .assertSignInButtonDisplayed()
            .clickSettingsButton()

        settingsRobot
            .assertSettingsScreenDisplayed()
            .openBackupPage()
            .clickBackupFileNameRow()
            .replaceBackupFileName("discarded")
            .clickBackupFileNameCancel()
            .assertBackupFileName("tvmaniac-backup.json")
    }

    @Test
    fun givenConnectedAccount_whenBackupRestoreRequested_thenDeviceOnlyChoiceIsReachable() = runAppFlowTest {
        scenarios.discover.stubBrowseGraph()

        discoverRobot
            .assertDiscoverScreenDisplayed()

        scenarios.signInAndDismissRationale()

        homeRobot
            .clickProfileTab()
            .assertTabSelected(HomeTestTags.PROFILE_TAB)

        profileRobot
            .assertUserCardDisplayed("integration-test-user")
            .clickSettingsButton()

        settingsRobot
            .assertSettingsScreenDisplayed()
            .openBackupPage()
            .clickBackupImportRow()
            .assertBackupRestoreConfirmDisplayed()
            .assertBackupRestoreDeviceOnlyDisplayed()
            .clickBackupRestoreDeviceOnly()
            .assertBackupRestoreConfirmDoesNotExist()
    }
}
