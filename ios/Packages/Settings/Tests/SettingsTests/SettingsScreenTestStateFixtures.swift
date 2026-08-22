import Components
import DesignSystem
import Models
import Settings
import SnapshotTestingLib
import SwiftUI
import XCTest

extension SettingsScreenTest {
    var behaviorToggles: [SettingsToggleItem] {
        [
            SettingsToggleItem(
                id: "sync",
                icon: "arrow.triangle.2.circlepath",
                title: "Background Sync",
                subtitle: "Sync your library in the background",
                isOn: false,
                onToggle: { _ in }
            ),
            SettingsToggleItem(
                id: "specials",
                icon: "film.stack",
                title: "Include Specials",
                subtitle: "Show special episodes in season lists",
                isOn: true,
                onToggle: { _ in }
            ),
            SettingsToggleItem(
                id: "quick-rate",
                icon: "star.fill",
                title: "Quick Rate",
                subtitle: "Ask for a rating after you mark an episode as watched",
                isOn: false,
                onToggle: { _ in }
            ),
            SettingsToggleItem(
                id: "youtube",
                icon: "tv",
                title: "Open in YouTube",
                subtitle: "Open trailers in the YouTube app",
                isOn: false,
                onToggle: { _ in }
            ),
        ]
    }

    var behaviorLockedToggles: [SettingsToggleItem] {
        behaviorToggles.map { toggle in
            guard toggle.id == "quick-rate" else { return toggle }
            return SettingsToggleItem(
                id: toggle.id,
                icon: toggle.icon,
                title: toggle.title,
                subtitle: toggle.subtitle,
                isOn: toggle.isOn,
                isLocked: true,
                lockedBadgeText: "Premium",
                lockedAccessibilityLabel: "Locked",
                onToggle: { _ in }
            )
        }
    }

    var notificationToggles: [SettingsToggleItem] {
        [
            SettingsToggleItem(
                id: "notifications",
                icon: "bell.fill",
                title: "Episode Notifications",
                subtitle: "Get notified when new episodes air",
                isOn: true,
                onToggle: { _ in }
            ),
        ]
    }

    var lockedNotificationToggles: [SettingsToggleItem] {
        [
            SettingsToggleItem(
                id: "notifications",
                icon: "bell.fill",
                title: "Episode Notifications",
                subtitle: "Get notified when new episodes air",
                isOn: false,
                isLocked: true,
                lockedBadgeText: "Premium",
                lockedAccessibilityLabel: "Locked",
                onToggle: { _ in }
            ),
        ]
    }

    var privacyToggles: [SettingsToggleItem] {
        [
            SettingsToggleItem(
                id: "crash-reporting",
                icon: "ladybug",
                title: "Crash Reporting",
                subtitle: "Help improve the app by sending crash reports",
                isOn: true,
                onToggle: { _ in }
            ),
        ]
    }

    var privacyLinks: [SettingsNavigationItem] {
        [
            SettingsNavigationItem(
                id: "privacy-policy",
                icon: "hand.raised",
                title: "Privacy Policy",
                onTap: {}
            ),
        ]
    }

    var infoContent: SettingsInfoContent {
        SettingsInfoContent(
            icon: Image(systemName: "app.fill"),
            appName: "TvManiac",
            versionText: "Version 1.0.0",
            description: "A Kotlin Multiplatform app for discovering and tracking your favorite TV shows.",
            sourceCodeLabel: "Source Code",
            sourceCodeValue: "GitHub",
            apiDisclaimer: "This product uses the TMDB and Trakt API but is not endorsed or certified by either.",
            onVersionTap: {},
            onSourceCodeTap: {}
        )
    }

    var licenseSections: [SettingsLicenseSection] {
        [
            SettingsLicenseSection(id: "app", label: "App", items: [
                SettingsLinkItem(
                    id: "tvmaniac",
                    title: "TvManiac",
                    body: "Open-source on GitHub.",
                    link: "https://github.com/c0de-wizard/tv-maniac",
                    onOpen: {}
                ),
            ]),
            SettingsLicenseSection(id: "data", label: "Data & Services", items: [
                SettingsLinkItem(
                    id: "tmdb",
                    leadingAsset: "TmdbLogo",
                    title: "The Movie Database (TMDB)",
                    body: "This product uses the TMDB API but is not endorsed or certified by TMDB.",
                    link: "https://www.themoviedb.org",
                    onOpen: {}
                ),
                SettingsLinkItem(
                    id: "trakt",
                    leadingAsset: "TraktLogo",
                    title: "Trakt",
                    body: "Syncs your watch history, watchlist, and episode progress.",
                    link: "https://trakt.tv",
                    onOpen: {}
                ),
            ]),
        ]
    }

    func accountContent(
        authenticated: Bool,
        withSwitchAffordance: Bool = false,
        isSwitching: Bool = false,
        showSwitchConfirmation: Bool = false,
        isProcessingAuth: Bool = false
    ) -> SettingsAccountContent {
        let switchLabel: String? = withSwitchAffordance || isSwitching || showSwitchConfirmation
            ? "Switch to Simkl"
            : nil
        return SettingsAccountContent(
            title: "Trakt",
            description: "Sync your watchlist, watch progress, continue watching, and personal lists with Trakt.",
            authenticationLabel: "Connect & Sync Your Content",
            connectTitle: "Connect",
            syncDescription: "Save your progress, discover new titles, and sync your content across all devices.",
            connectedTitle: authenticated ? "Connected" : "Connect to Trakt",
            connectedDescription: authenticated
                ? "Your watch history, watchlist, and episode progress sync with Trakt."
                : "Sign in with Trakt to sync your watch history, watchlist, and episode progress across your devices.",
            isAuthenticated: authenticated,
            isProcessingAuth: isProcessingAuth,
            logoutLabel: "Logout",
            loginLabel: "Login",
            providerName: "Trakt",
            authProviders: [
                SwiftAuthProvider(id: "TRAKT", label: "Continue with Trakt", logoName: "TraktMono"),
                SwiftAuthProvider(id: "SIMKL", label: "Continue with Simkl", logoName: "SimklMono"),
            ],
            switchTargetLogoName: switchLabel != nil ? "SimklMono" : nil,
            switchActionLabel: switchLabel,
            isSwitching: isSwitching,
            showSwitchConfirmation: showSwitchConfirmation,
            switchDialogTitle: showSwitchConfirmation ? "Switch to Simkl?" : nil,
            switchDialogMessage: showSwitchConfirmation
                ? "You have 3 unsaved changes. Switching providers will discard them."
                : nil,
            switchConfirmLabel: "Switch",
            switchCancelLabel: "Cancel",
            switchingLabel: "Switching...",
            onLogout: {},
            onProviderSelected: { _ in },
            onSwitchProvider: {},
            onConfirmSwitch: {},
            onDismissSwitchDialog: {}
        )
    }

    func autoBackupContent(
        isOn: Bool = false,
        lastRunLabel: String = "No backup saved yet",
        failureWarning: String? = nil,
        selectedSchedule: String = "WEEKLY"
    ) -> SettingsAutoBackupContent {
        SettingsAutoBackupContent(
            title: "Automatic backup",
            description: "Save your shows, watch history, ratings and settings to a file on a schedule",
            isOn: isOn,
            locationTitle: "Backup location",
            locationLabel: "Downloads",
            hasLocation: true,
            fileNameTitle: "File name",
            fileNameMessage: "Backups are saved under this name",
            fileName: "tvmaniac-backup.json",
            fileNameSaveLabel: "Save",
            fileNameCancelLabel: "Cancel",
            scheduleTitle: "How often",
            scheduleOptions: [
                ("DAILY", "Every day"),
                ("WEEKLY", "Every week"),
                ("FORTNIGHTLY", "Every two weeks"),
                ("MONTHLY", "Every month"),
            ].map { id, label in
                SettingsAutoBackupScheduleOption(
                    id: id,
                    label: label,
                    isSelected: id == selectedSchedule,
                    onSelect: {}
                )
            },
            lastRunLabel: lastRunLabel,
            failureWarning: failureWarning,
            backupNowTitle: "Back up now",
            backupNowDescription: "Save a backup straight away",
            onToggle: { _ in },
            onBackupNow: {}
        )
    }

    func backupContent(
        locked: Bool = false,
        isExporting: Bool = false,
        isImporting: Bool = false,
        summary: SettingsBackupSummaryContent? = nil,
        autoBackup: SettingsAutoBackupContent? = nil
    ) -> SettingsBackupContent {
        SettingsBackupContent(
            exportTitle: "Save a backup",
            exportDescription: "Write your shows, watch history, ratings and settings to a file",
            isExporting: isExporting,
            importTitle: "Restore a backup",
            importDescription: "Replace your shows, watch history, ratings and settings from a file",
            isImporting: isImporting,
            summary: summary,
            summaryDismissAccessibilityLabel: "Dismiss",
            autoBackup: autoBackup ?? autoBackupContent(),
            isLocked: locked,
            lockedBadgeText: locked ? "Premium" : "",
            lockedTitle: locked ? "Backup is a Premium feature" : "",
            lockedMessage: locked ? "Upgrade to Premium to save and restore your shows." : "",
            lockedActionText: locked ? "Upgrade to Premium" : "",
            lockedAccessibilityLabel: locked ? "Locked" : "",
            onExport: {},
            onImport: {},
            onUpgradeClick: {},
            onDismissSummary: {}
        )
    }

    var restoreSummaryContent: SettingsBackupSummaryContent {
        SettingsBackupSummaryContent(
            title: "Restore complete",
            showsRestored: "48 shows restored",
            episodesRestored: "612 episodes restored"
        )
    }

    var restoreSummaryContentWithSkips: SettingsBackupSummaryContent {
        SettingsBackupSummaryContent(
            title: "Restore complete",
            showsRestored: "45 shows restored",
            episodesRestored: "598 episodes restored",
            showsSkipped: "3 shows couldn't be restored",
            skippedShows: ["Severance", "The Bear", "Shōgun"],
            rewatchNotice: "Rewatch history wasn't restored."
        )
    }

    func rootSections(authenticated: Bool) -> [SettingsRootSection] {
        var sections: [SettingsRootSection] = []
        if authenticated {
            sections.append(SettingsRootSection(id: "account", label: "Account", items: [
                navItem(.account, "Trakt Account", "Manage your Trakt connection"),
            ]))
        }
        sections.append(SettingsRootSection(id: "general", label: "General", items: [
            navItem(.appearance, "Appearance", "Theme and image quality"),
            navItem(.layout, "Layout", "Personalize how shows and episodes look"),
            navItem(.behavior, "Behavior", "Sync, specials, and trailers"),
            navItem(.notifications, "Notifications", "Episode release alerts"),
            navItem(.privacy, "Privacy", "Crash reporting and privacy policy"),
        ]))
        sections.append(SettingsRootSection(id: "about", label: "About", items: [
            navItem(.info, "Info", "App version and source code"),
            navItem(.licenses, "Licenses & Attribution", "Data sources and acknowledgements"),
        ]))
        return sections
    }

    func navItem(_ route: SettingsPageRoute, _ title: String, _ subtitle: String) -> SettingsNavigationItem {
        SettingsNavigationItem(id: route.rawValue, icon: route.iconName, title: title, subtitle: subtitle, onTap: {})
    }

    func makeState(
        page: SettingsPageRoute,
        authenticated: Bool,
        isLoading: Bool = false,
        customAccountContent: SettingsAccountContent? = nil,
        customThemeItem: SettingsThemeItem<ThemeItemModel>? = nil,
        customNotificationToggles: [SettingsToggleItem]? = nil,
        customBehaviorToggles: [SettingsToggleItem]? = nil,
        fontSizePercent: Int = 100,
        customPosterStyleItem: SettingsPosterStyleItem? = nil,
        customBackupContent: SettingsBackupContent? = nil
    ) -> SettingsScreen<ThemeItemModel>.State {
        SettingsScreen<ThemeItemModel>.State(
            isLoading: isLoading,
            rootTitle: "Settings",
            currentPage: page,
            rootSections: rootSections(authenticated: authenticated),
            themeItem: customThemeItem ?? defaultThemeItem,
            imageQualityItem: defaultImageQualityItem,
            layoutToggles: layoutToggles,
            fontSizeItem: fontSizeItem(percent: fontSizePercent),
            discoverSectionsNavItem: discoverSectionsNavItem,
            discoverSectionToggles: discoverSectionToggles,
            posterStyleNavItem: posterStyleNavItem,
            posterStyleItem: customPosterStyleItem ?? posterStyleItem(),
            behaviorToggles: customBehaviorToggles ?? behaviorToggles,
            notificationToggles: customNotificationToggles ?? notificationToggles,
            privacyToggles: privacyToggles,
            privacyLinks: privacyLinks,
            infoContent: infoContent,
            licenseSections: licenseSections,
            accountContent: customAccountContent ?? accountContent(authenticated: authenticated),
            backupContent: customBackupContent ?? backupContent()
        )
    }
}
