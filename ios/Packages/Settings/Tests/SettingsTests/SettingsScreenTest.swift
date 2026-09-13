import Components
import DesignSystem
import Models
import Settings
import SnapshotTestingLib
import SwiftUI
import XCTest

class SettingsScreenTest: SnapshotTestCase {
    func test_SettingsScreen_Loading() {
        let view = SettingsScreen(state: makeState(page: .root, authenticated: false, isLoading: true), onBack: {})
            .appPreview()
        view.assertSnapshot(layout: .defaultDevice, testName: "SettingsScreen_Loading")
        view.assertSnapshot(layout: .defaultDevice, liquidGlass: true, testName: "SettingsScreen_Loading")
    }

    func test_SettingsScreen_Root() {
        let view = SettingsScreen(state: makeState(page: .root, authenticated: false), onBack: {})
            .appPreview()
        view.assertSnapshot(layout: .defaultDevice, testName: "SettingsScreen_Root")
        view.assertSnapshot(layout: .defaultDevice, liquidGlass: true, testName: "SettingsScreen_Root")
    }

    func test_SettingsScreen_RootAuthenticated() {
        let view = SettingsScreen(state: makeState(page: .root, authenticated: true), onBack: {})
            .appPreview()
        view.assertSnapshot(layout: .defaultDevice, testName: "SettingsScreen_RootAuthenticated")
        view.assertSnapshot(layout: .defaultDevice, liquidGlass: true, testName: "SettingsScreen_RootAuthenticated")
    }

    func test_SettingsScreen_Layout() {
        let view = SettingsScreen(state: makeState(page: .layout, authenticated: true), onBack: {})
            .appPreview()
        view.assertSnapshot(layout: .defaultDevice, testName: "SettingsScreen_Layout")
        view.assertSnapshot(layout: .defaultDevice, liquidGlass: true, testName: "SettingsScreen_Layout")
    }

    func test_SettingsScreen_Layout_FontScaled() {
        TvManiacTypographyScheme.updateFontScale(percent: 130)
        defer { TvManiacTypographyScheme.updateFontScale(percent: 100) }

        let view = SettingsScreen(
            state: makeState(page: .layout, authenticated: true, fontSizePercent: 130),
            onBack: {}
        )
        .appPreview()
        view.assertSnapshot(layout: .defaultDevice, testName: "SettingsScreen_Layout_FontScaled")
        view.assertSnapshot(layout: .defaultDevice, liquidGlass: true, testName: "SettingsScreen_Layout_FontScaled")
    }

    func test_SettingsScreen_Root_DynamicTypeXXXLarge() {
        let view = SettingsScreen(state: makeState(page: .root, authenticated: true), onBack: {})
            .appPreview()
            .environment(\.dynamicTypeSize, .xxxLarge)
        view.assertSnapshot(layout: .defaultDevice, styles: .dark, testName: "SettingsScreen_Root_DynamicTypeXXXLarge")
        view.assertSnapshot(layout: .defaultDevice, styles: .dark, liquidGlass: true, testName: "SettingsScreen_Root_DynamicTypeXXXLarge")
    }

    func test_SettingsScreen_Root_DynamicTypeAX3() {
        let view = SettingsScreen(state: makeState(page: .root, authenticated: true), onBack: {})
            .appPreview()
            .environment(\.dynamicTypeSize, .accessibility3)
        view.assertSnapshot(layout: .defaultDevice, styles: .dark, testName: "SettingsScreen_Root_DynamicTypeAX3")
        view.assertSnapshot(layout: .defaultDevice, styles: .dark, liquidGlass: true, testName: "SettingsScreen_Root_DynamicTypeAX3")
    }

    func test_SettingsScreen_DiscoverSections() {
        let view = SettingsScreen(state: makeState(page: .discoverSections, authenticated: true), onBack: {})
            .appPreview()
        view.assertSnapshot(layout: .defaultDevice, testName: "SettingsScreen_DiscoverSections")
        view.assertSnapshot(layout: .defaultDevice, liquidGlass: true, testName: "SettingsScreen_DiscoverSections")
    }

    func test_SettingsScreen_PosterStyle() {
        let view = SettingsScreen(state: makeState(page: .posterStyle, authenticated: true), onBack: {})
            .appPreview()
        view.assertSnapshot(layout: .defaultDevice, testName: "SettingsScreen_PosterStyle")
        view.assertSnapshot(layout: .defaultDevice, liquidGlass: true, testName: "SettingsScreen_PosterStyle")
    }

    func test_SettingsScreen_PosterStyle_Locked() {
        let view = SettingsScreen(
            state: makeState(
                page: .posterStyle,
                authenticated: true,
                customPosterStyleItem: posterStyleItem(locked: true)
            ),
            onBack: {}
        )
        .appPreview()
        view.assertSnapshot(layout: .defaultDevice, testName: "SettingsScreen_PosterStyle_Locked")
        view.assertSnapshot(layout: .defaultDevice, liquidGlass: true, testName: "SettingsScreen_PosterStyle_Locked")
    }

    func test_SettingsScreen_WidgetAppearance() {
        let view = SettingsScreen(
            state: makeState(
                page: .widgetAppearance,
                authenticated: true,
                customWidgetAppearanceItem: widgetAppearanceItem(
                    selectedOptionId: "crimson",
                    previewTheme: CrimsonTheme()
                )
            ),
            onBack: {}
        )
        .appPreview()
        view.assertSnapshot(layout: .defaultDevice, testName: "SettingsScreen_WidgetAppearance")
        view.assertSnapshot(layout: .defaultDevice, liquidGlass: true, testName: "SettingsScreen_WidgetAppearance")
    }

    func test_SettingsScreen_WidgetAppearance_Locked() {
        let view = SettingsScreen(
            state: makeState(
                page: .widgetAppearance,
                authenticated: true,
                customWidgetAppearanceItem: widgetAppearanceItem(isLocked: true)
            ),
            onBack: {}
        )
        .appPreview()
        view.assertSnapshot(layout: .defaultDevice, testName: "SettingsScreen_WidgetAppearance_Locked")
        view.assertSnapshot(layout: .defaultDevice, liquidGlass: true, testName: "SettingsScreen_WidgetAppearance_Locked")
    }

    func test_SettingsScreen_Appearance() {
        let view = SettingsScreen(state: makeState(page: .appearance, authenticated: true), onBack: {})
            .appPreview()
        view.assertSnapshot(layout: .defaultDevice, testName: "SettingsScreen_Appearance")
        view.assertSnapshot(layout: .defaultDevice, liquidGlass: true, testName: "SettingsScreen_Appearance")
    }

    func test_SettingsScreen_Appearance_Locked() {
        let view = SettingsScreen(
            state: makeState(
                page: .appearance,
                authenticated: true,
                customThemeItem: customThemeItem
            ),
            onBack: {}
        )
        .appPreview()
        view.assertSnapshot(layout: .defaultDevice, testName: "SettingsScreen_Appearance_Locked")
        view.assertSnapshot(layout: .defaultDevice, liquidGlass: true, testName: "SettingsScreen_Appearance_Locked")
    }

    func test_SettingsScreen_Behavior() {
        let view = SettingsScreen(state: makeState(page: .behavior, authenticated: true), onBack: {})
            .appPreview()
        view.assertSnapshot(layout: .defaultDevice, testName: "SettingsScreen_Behavior")
        view.assertSnapshot(layout: .defaultDevice, liquidGlass: true, testName: "SettingsScreen_Behavior")
    }

    func test_SettingsScreen_Behavior_Locked() {
        let view = SettingsScreen(
            state: makeState(
                page: .behavior,
                authenticated: true,
                customBehaviorToggles: behaviorLockedToggles
            ),
            onBack: {}
        )
        .appPreview()
        view.assertSnapshot(layout: .defaultDevice, testName: "SettingsScreen_Behavior_Locked")
        view.assertSnapshot(layout: .defaultDevice, liquidGlass: true, testName: "SettingsScreen_Behavior_Locked")
    }

    func test_SettingsScreen_Notifications() {
        let view = SettingsScreen(state: makeState(page: .notifications, authenticated: true), onBack: {})
            .appPreview()
        view.assertSnapshot(layout: .defaultDevice, testName: "SettingsScreen_Notifications")
        view.assertSnapshot(layout: .defaultDevice, liquidGlass: true, testName: "SettingsScreen_Notifications")
    }

    func test_SettingsScreen_Notifications_Locked() {
        let view = SettingsScreen(
            state: makeState(
                page: .notifications,
                authenticated: true,
                customNotificationToggles: lockedNotificationToggles
            ),
            onBack: {}
        )
        .appPreview()
        view.assertSnapshot(layout: .defaultDevice, testName: "SettingsScreen_Notifications_Locked")
        view.assertSnapshot(layout: .defaultDevice, liquidGlass: true, testName: "SettingsScreen_Notifications_Locked")
    }

    func test_SettingsScreen_Privacy() {
        let view = SettingsScreen(state: makeState(page: .privacy, authenticated: true), onBack: {})
            .appPreview()
        view.assertSnapshot(layout: .defaultDevice, testName: "SettingsScreen_Privacy")
        view.assertSnapshot(layout: .defaultDevice, liquidGlass: true, testName: "SettingsScreen_Privacy")
    }

    func test_SettingsScreen_Info() {
        let view = SettingsScreen(state: makeState(page: .info, authenticated: true), onBack: {})
            .appPreview()
        view.assertSnapshot(layout: .defaultDevice, testName: "SettingsScreen_Info")
        view.assertSnapshot(layout: .defaultDevice, liquidGlass: true, testName: "SettingsScreen_Info")
    }

    func test_SettingsScreen_Licenses() {
        let view = SettingsScreen(state: makeState(page: .licenses, authenticated: true), onBack: {})
            .appPreview()
        view.assertSnapshot(layout: .defaultDevice, testName: "SettingsScreen_Licenses")
        view.assertSnapshot(layout: .defaultDevice, liquidGlass: true, testName: "SettingsScreen_Licenses")
    }

    func test_SettingsScreen_Trakt() {
        let view = SettingsScreen(state: makeState(page: .account, authenticated: true), onBack: {})
            .appPreview()
        view.assertSnapshot(layout: .defaultDevice, testName: "SettingsScreen_Trakt")
        view.assertSnapshot(layout: .defaultDevice, liquidGlass: true, testName: "SettingsScreen_Trakt")
    }

    func test_SettingsScreen_TraktLoggedOut() {
        let view = SettingsScreen(state: makeState(page: .account, authenticated: false), onBack: {})
            .appPreview()
        view.assertSnapshot(layout: .defaultDevice, testName: "SettingsScreen_TraktLoggedOut")
        view.assertSnapshot(layout: .defaultDevice, liquidGlass: true, testName: "SettingsScreen_TraktLoggedOut")
    }

    func test_SettingsScreen_Account_SwitchAffordance() {
        let content = accountContent(authenticated: true, withSwitchAffordance: true)
        let view = SettingsScreen(
            state: makeState(page: .account, authenticated: true, customAccountContent: content),
            onBack: {}
        )
        .appPreview()
        view.assertSnapshot(layout: .defaultDevice, testName: "SettingsScreen_Account_SwitchAffordance")
        view.assertSnapshot(layout: .defaultDevice, liquidGlass: true, testName: "SettingsScreen_Account_SwitchAffordance")
    }

    func test_SettingsScreen_Account_Switching() {
        let content = accountContent(authenticated: true, isSwitching: true)
        let view = SettingsScreen(
            state: makeState(page: .account, authenticated: true, customAccountContent: content),
            onBack: {}
        )
        .appPreview()
        view.assertSnapshot(layout: .defaultDevice, testName: "SettingsScreen_Account_Switching")
        view.assertSnapshot(layout: .defaultDevice, liquidGlass: true, testName: "SettingsScreen_Account_Switching")
    }

    func test_SettingsScreen_Account_LoggingOut() {
        let content = accountContent(authenticated: true, isProcessingAuth: true)
        let view = SettingsScreen(
            state: makeState(page: .account, authenticated: true, customAccountContent: content),
            onBack: {}
        )
        .appPreview()
        view.assertSnapshot(layout: .defaultDevice, testName: "SettingsScreen_Account_LoggingOut")
        view.assertSnapshot(layout: .defaultDevice, liquidGlass: true, testName: "SettingsScreen_Account_LoggingOut")
    }

    func test_SettingsScreen_Account_SwitchConfirmDialog() {
        let content = accountContent(authenticated: true, showSwitchConfirmation: true)
        let view = SettingsScreen(
            state: makeState(page: .account, authenticated: true, customAccountContent: content),
            onBack: {}
        )
        .appPreview()
        view.assertSnapshot(layout: .defaultDevice, testName: "SettingsScreen_Account_SwitchConfirmDialog")
        view.assertSnapshot(layout: .defaultDevice, liquidGlass: true, testName: "SettingsScreen_Account_SwitchConfirmDialog")
    }

    func test_SettingsScreen_Backup() {
        let view = SettingsScreen(state: makeState(page: .backup, authenticated: true), onBack: {})
            .appPreview()
        view.assertSnapshot(layout: .defaultDevice, testName: "SettingsScreen_Backup")
        view.assertSnapshot(layout: .defaultDevice, liquidGlass: true, testName: "SettingsScreen_Backup")
    }

    func test_SettingsScreen_Backup_Locked() {
        let view = SettingsScreen(
            state: makeState(page: .backup, authenticated: true, customBackupContent: backupContent(locked: true)),
            onBack: {}
        )
        .appPreview()
        view.assertSnapshot(layout: .defaultDevice, testName: "SettingsScreen_Backup_Locked")
        view.assertSnapshot(layout: .defaultDevice, liquidGlass: true, testName: "SettingsScreen_Backup_Locked")
    }

    func test_SettingsScreen_Backup_Exporting() {
        let view = SettingsScreen(
            state: makeState(page: .backup, authenticated: true, customBackupContent: backupContent(isExporting: true)),
            onBack: {}
        )
        .appPreview()
        view.assertSnapshot(layout: .defaultDevice, testName: "SettingsScreen_Backup_Exporting")
        view.assertSnapshot(layout: .defaultDevice, liquidGlass: true, testName: "SettingsScreen_Backup_Exporting")
    }

    func test_SettingsScreen_Backup_Importing() {
        let view = SettingsScreen(
            state: makeState(page: .backup, authenticated: true, customBackupContent: backupContent(isImporting: true)),
            onBack: {}
        )
        .appPreview()
        view.assertSnapshot(layout: .defaultDevice, testName: "SettingsScreen_Backup_Importing")
        view.assertSnapshot(layout: .defaultDevice, liquidGlass: true, testName: "SettingsScreen_Backup_Importing")
    }

    func test_SettingsScreen_Backup_AutoBackupOn() {
        let view = SettingsScreen(
            state: makeState(
                page: .backup,
                authenticated: true,
                customBackupContent: backupContent(
                    autoBackup: autoBackupContent(isOn: true, lastRunLabel: "Last backup 12 August 2026")
                )
            ),
            onBack: {}
        )
        .appPreview()
        view.assertSnapshot(layout: .defaultDevice, testName: "SettingsScreen_Backup_AutoBackupOn")
        view.assertSnapshot(layout: .defaultDevice, liquidGlass: true, testName: "SettingsScreen_Backup_AutoBackupOn")
    }

    func test_SettingsScreen_Backup_AutoBackupNeverRun() {
        let view = SettingsScreen(
            state: makeState(
                page: .backup,
                authenticated: true,
                customBackupContent: backupContent(autoBackup: autoBackupContent(isOn: true))
            ),
            onBack: {}
        )
        .appPreview()
        view.assertSnapshot(layout: .defaultDevice, testName: "SettingsScreen_Backup_AutoBackupNeverRun")
        view.assertSnapshot(layout: .defaultDevice, liquidGlass: true, testName: "SettingsScreen_Backup_AutoBackupNeverRun")
    }

    func test_SettingsScreen_Backup_AutoBackupFailed() {
        let view = SettingsScreen(
            state: makeState(
                page: .backup,
                authenticated: true,
                customBackupContent: backupContent(
                    autoBackup: autoBackupContent(
                        isOn: true,
                        lastRunLabel: "Last backup 12 August 2026",
                        failureWarning: "The last automatic backup failed. Check the location is still available, then back up now.",
                        selectedSchedule: "MONTHLY"
                    )
                )
            ),
            onBack: {}
        )
        .appPreview()
        view.assertSnapshot(layout: .defaultDevice, testName: "SettingsScreen_Backup_AutoBackupFailed")
        view.assertSnapshot(layout: .defaultDevice, liquidGlass: true, testName: "SettingsScreen_Backup_AutoBackupFailed")
    }

    func test_SettingsScreen_Backup_RestoreSummary() {
        let view = SettingsScreen(
            state: makeState(page: .backup, authenticated: true, customBackupContent: backupContent(summary: restoreSummaryContent)),
            onBack: {}
        )
        .appPreview()
        view.assertSnapshot(layout: .defaultDevice, testName: "SettingsScreen_Backup_RestoreSummary")
        view.assertSnapshot(layout: .defaultDevice, liquidGlass: true, testName: "SettingsScreen_Backup_RestoreSummary")
    }

    func test_SettingsScreen_Backup_RestoreSummary_WithSkips() {
        let view = SettingsScreen(
            state: makeState(page: .backup, authenticated: true, customBackupContent: backupContent(summary: restoreSummaryContentWithSkips)),
            onBack: {}
        )
        .appPreview()
        view.assertSnapshot(layout: .defaultDevice, testName: "SettingsScreen_Backup_RestoreSummary_WithSkips")
        view.assertSnapshot(layout: .defaultDevice, liquidGlass: true, testName: "SettingsScreen_Backup_RestoreSummary_WithSkips")
    }
}
