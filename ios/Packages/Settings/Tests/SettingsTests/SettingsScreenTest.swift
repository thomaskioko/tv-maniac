import Components
import DesignSystem
import Models
import Settings
import SnapshotTestingLib
import SwiftUI
import XCTest

class SettingsScreenTest: SnapshotTestCase {
    func test_SettingsScreen_Loading() {
        SettingsScreen(state: makeState(page: .root, authenticated: false, isLoading: true), onBack: {})
            .appPreview()
            .assertSnapshot(layout: .defaultDevice, testName: "SettingsScreen_Loading")
    }

    func test_SettingsScreen_Root() {
        SettingsScreen(state: makeState(page: .root, authenticated: false), onBack: {})
            .appPreview()
            .assertSnapshot(layout: .defaultDevice, testName: "SettingsScreen_Root")
    }

    func test_SettingsScreen_RootAuthenticated() {
        SettingsScreen(state: makeState(page: .root, authenticated: true), onBack: {})
            .appPreview()
            .assertSnapshot(layout: .defaultDevice, testName: "SettingsScreen_RootAuthenticated")
    }

    func test_SettingsScreen_Layout() {
        SettingsScreen(state: makeState(page: .layout, authenticated: true), onBack: {})
            .appPreview()
            .assertSnapshot(layout: .defaultDevice, testName: "SettingsScreen_Layout")
    }

    func test_SettingsScreen_Layout_FontScaled() {
        TvManiacTypographyScheme.updateFontScale(percent: 130)
        defer { TvManiacTypographyScheme.updateFontScale(percent: 100) }

        SettingsScreen(
            state: makeState(page: .layout, authenticated: true, fontSizePercent: 130),
            onBack: {}
        )
        .appPreview()
        .assertSnapshot(layout: .defaultDevice, testName: "SettingsScreen_Layout_FontScaled")
    }

    func test_SettingsScreen_Root_DynamicTypeXXXLarge() {
        SettingsScreen(state: makeState(page: .root, authenticated: true), onBack: {})
            .appPreview()
            .environment(\.dynamicTypeSize, .xxxLarge)
            .assertSnapshot(layout: .defaultDevice, styles: .dark, testName: "SettingsScreen_Root_DynamicTypeXXXLarge")
    }

    func test_SettingsScreen_Root_DynamicTypeAX3() {
        SettingsScreen(state: makeState(page: .root, authenticated: true), onBack: {})
            .appPreview()
            .environment(\.dynamicTypeSize, .accessibility3)
            .assertSnapshot(layout: .defaultDevice, styles: .dark, testName: "SettingsScreen_Root_DynamicTypeAX3")
    }

    func test_SettingsScreen_DiscoverSections() {
        SettingsScreen(state: makeState(page: .discoverSections, authenticated: true), onBack: {})
            .appPreview()
            .assertSnapshot(layout: .defaultDevice, testName: "SettingsScreen_DiscoverSections")
    }

    func test_SettingsScreen_PosterStyle() {
        SettingsScreen(state: makeState(page: .posterStyle, authenticated: true), onBack: {})
            .appPreview()
            .assertSnapshot(layout: .defaultDevice, testName: "SettingsScreen_PosterStyle")
    }

    func test_SettingsScreen_PosterStyle_Locked() {
        SettingsScreen(
            state: makeState(
                page: .posterStyle,
                authenticated: true,
                customPosterStyleItem: posterStyleItem(locked: true)
            ),
            onBack: {}
        )
        .appPreview()
        .assertSnapshot(layout: .defaultDevice, testName: "SettingsScreen_PosterStyle_Locked")
    }

    func test_SettingsScreen_Appearance() {
        SettingsScreen(state: makeState(page: .appearance, authenticated: true), onBack: {})
            .appPreview()
            .assertSnapshot(layout: .defaultDevice, testName: "SettingsScreen_Appearance")
    }

    func test_SettingsScreen_Appearance_Locked() {
        SettingsScreen(
            state: makeState(
                page: .appearance,
                authenticated: true,
                customThemeItem: customThemeItem
            ),
            onBack: {}
        )
        .appPreview()
        .assertSnapshot(layout: .defaultDevice, testName: "SettingsScreen_Appearance_Locked")
    }

    func test_SettingsScreen_Behavior() {
        SettingsScreen(state: makeState(page: .behavior, authenticated: true), onBack: {})
            .appPreview()
            .assertSnapshot(layout: .defaultDevice, testName: "SettingsScreen_Behavior")
    }

    func test_SettingsScreen_Behavior_Locked() {
        SettingsScreen(
            state: makeState(
                page: .behavior,
                authenticated: true,
                customBehaviorToggles: behaviorLockedToggles
            ),
            onBack: {}
        )
        .appPreview()
        .assertSnapshot(layout: .defaultDevice, testName: "SettingsScreen_Behavior_Locked")
    }

    func test_SettingsScreen_Notifications() {
        SettingsScreen(state: makeState(page: .notifications, authenticated: true), onBack: {})
            .appPreview()
            .assertSnapshot(layout: .defaultDevice, testName: "SettingsScreen_Notifications")
    }

    func test_SettingsScreen_Notifications_Locked() {
        SettingsScreen(
            state: makeState(
                page: .notifications,
                authenticated: true,
                customNotificationToggles: lockedNotificationToggles
            ),
            onBack: {}
        )
        .appPreview()
        .assertSnapshot(layout: .defaultDevice, testName: "SettingsScreen_Notifications_Locked")
    }

    func test_SettingsScreen_Privacy() {
        SettingsScreen(state: makeState(page: .privacy, authenticated: true), onBack: {})
            .appPreview()
            .assertSnapshot(layout: .defaultDevice, testName: "SettingsScreen_Privacy")
    }

    func test_SettingsScreen_Info() {
        SettingsScreen(state: makeState(page: .info, authenticated: true), onBack: {})
            .appPreview()
            .assertSnapshot(layout: .defaultDevice, testName: "SettingsScreen_Info")
    }

    func test_SettingsScreen_Licenses() {
        SettingsScreen(state: makeState(page: .licenses, authenticated: true), onBack: {})
            .appPreview()
            .assertSnapshot(layout: .defaultDevice, testName: "SettingsScreen_Licenses")
    }

    func test_SettingsScreen_Trakt() {
        SettingsScreen(state: makeState(page: .account, authenticated: true), onBack: {})
            .appPreview()
            .assertSnapshot(layout: .defaultDevice, testName: "SettingsScreen_Trakt")
    }

    func test_SettingsScreen_TraktLoggedOut() {
        SettingsScreen(state: makeState(page: .account, authenticated: false), onBack: {})
            .appPreview()
            .assertSnapshot(layout: .defaultDevice, testName: "SettingsScreen_TraktLoggedOut")
    }

    func test_SettingsScreen_Account_SwitchAffordance() {
        let content = accountContent(authenticated: true, withSwitchAffordance: true)
        SettingsScreen(
            state: makeState(page: .account, authenticated: true, customAccountContent: content),
            onBack: {}
        )
        .appPreview()
        .assertSnapshot(layout: .defaultDevice, testName: "SettingsScreen_Account_SwitchAffordance")
    }

    func test_SettingsScreen_Account_Switching() {
        let content = accountContent(authenticated: true, isSwitching: true)
        SettingsScreen(
            state: makeState(page: .account, authenticated: true, customAccountContent: content),
            onBack: {}
        )
        .appPreview()
        .assertSnapshot(layout: .defaultDevice, testName: "SettingsScreen_Account_Switching")
    }

    func test_SettingsScreen_Account_LoggingOut() {
        let content = accountContent(authenticated: true, isProcessingAuth: true)
        SettingsScreen(
            state: makeState(page: .account, authenticated: true, customAccountContent: content),
            onBack: {}
        )
        .appPreview()
        .assertSnapshot(layout: .defaultDevice, testName: "SettingsScreen_Account_LoggingOut")
    }

    func test_SettingsScreen_Account_SwitchConfirmDialog() {
        let content = accountContent(authenticated: true, showSwitchConfirmation: true)
        SettingsScreen(
            state: makeState(page: .account, authenticated: true, customAccountContent: content),
            onBack: {}
        )
        .appPreview()
        .assertSnapshot(layout: .defaultDevice, testName: "SettingsScreen_Account_SwitchConfirmDialog")
    }

    func test_SettingsScreen_Backup() {
        SettingsScreen(state: makeState(page: .backup, authenticated: true), onBack: {})
            .appPreview()
            .assertSnapshot(layout: .defaultDevice, testName: "SettingsScreen_Backup")
    }

    func test_SettingsScreen_Backup_Locked() {
        SettingsScreen(
            state: makeState(page: .backup, authenticated: true, customBackupContent: backupContent(locked: true)),
            onBack: {}
        )
        .appPreview()
        .assertSnapshot(layout: .defaultDevice, testName: "SettingsScreen_Backup_Locked")
    }

    func test_SettingsScreen_Backup_Exporting() {
        SettingsScreen(
            state: makeState(page: .backup, authenticated: true, customBackupContent: backupContent(isExporting: true)),
            onBack: {}
        )
        .appPreview()
        .assertSnapshot(layout: .defaultDevice, testName: "SettingsScreen_Backup_Exporting")
    }

    func test_SettingsScreen_Backup_Importing() {
        SettingsScreen(
            state: makeState(page: .backup, authenticated: true, customBackupContent: backupContent(isImporting: true)),
            onBack: {}
        )
        .appPreview()
        .assertSnapshot(layout: .defaultDevice, testName: "SettingsScreen_Backup_Importing")
    }
}
