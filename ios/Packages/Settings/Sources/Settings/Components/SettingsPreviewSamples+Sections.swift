#if DEBUG
    import Components
    import DesignSystem
    import Models
    import SwiftUI

    extension SettingsPreviewSamples {
        static var discoverSectionsNavItem: SettingsNavigationItem {
            SettingsNavigationItem(
                id: SettingsPageRoute.discoverSections.rawValue,
                icon: SettingsPageRoute.discoverSections.iconName,
                title: "Discover Sections",
                subtitle: "Choose which sections appear on the Discover tab",
                onTap: {}
            )
        }

        static var discoverSectionToggles: [SettingsToggleItem] {
            [
                SettingsToggleItem(
                    id: "START_WATCHING",
                    icon: "play.circle",
                    title: "Start Watching",
                    subtitle: "",
                    isOn: true,
                    onToggle: { _ in }
                ),
                SettingsToggleItem(
                    id: "TRENDING_TODAY",
                    icon: "flame",
                    title: "Trending Today",
                    subtitle: "",
                    isOn: true,
                    onToggle: { _ in }
                ),
                SettingsToggleItem(
                    id: "UPCOMING",
                    icon: "calendar",
                    title: "Upcoming",
                    subtitle: "",
                    isOn: false,
                    onToggle: { _ in }
                ),
                SettingsToggleItem(
                    id: "POPULAR",
                    icon: "star",
                    title: "Popular",
                    subtitle: "",
                    isOn: true,
                    onToggle: { _ in }
                ),
                SettingsToggleItem(
                    id: "TOP_RATED",
                    icon: "trophy",
                    title: "Top Rated",
                    subtitle: "",
                    isOn: true,
                    onToggle: { _ in }
                ),
            ]
        }

        static var behaviorToggles: [SettingsToggleItem] {
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
                    id: "youtube",
                    icon: "tv",
                    title: "Open in YouTube",
                    subtitle: "Open trailers in the YouTube app",
                    isOn: false,
                    onToggle: { _ in }
                ),
            ]
        }

        static var notificationToggles: [SettingsToggleItem] {
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

        static var lockedNotificationToggles: [SettingsToggleItem] {
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

        static var privacyToggles: [SettingsToggleItem] {
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

        static var privacyLinks: [SettingsNavigationItem] {
            [
                SettingsNavigationItem(
                    id: "privacy-policy",
                    icon: "hand.raised",
                    title: "Privacy Policy",
                    onTap: {}
                ),
            ]
        }

        static var autoBackupContent: SettingsAutoBackupContent {
            SettingsAutoBackupContent(
                title: "Automatic backup",
                description: "Save your shows, watch history, ratings and settings to a file on a schedule",
                locationTitle: "Backup location",
                locationLabel: "tvmaniac-backup.json",
                hasLocation: true,
                scheduleTitle: "How often",
                scheduleOptions: autoBackupScheduleOptions(selected: "WEEKLY"),
                lastRunLabel: "No backup saved yet",
                backupNowTitle: "Back up now",
                backupNowDescription: "Save a backup straight away",
                onToggle: { _ in },
                onBackupNow: {}
            )
        }

        static var autoBackupOnContent: SettingsAutoBackupContent {
            SettingsAutoBackupContent(
                title: "Automatic backup",
                description: "Save your shows, watch history, ratings and settings to a file on a schedule",
                isOn: true,
                locationTitle: "Backup location",
                locationLabel: "tvmaniac-backup.json",
                hasLocation: true,
                scheduleTitle: "How often",
                scheduleOptions: autoBackupScheduleOptions(selected: "WEEKLY"),
                lastRunLabel: "Last backup 12 August 2026",
                backupNowTitle: "Back up now",
                backupNowDescription: "Save a backup straight away",
                onToggle: { _ in },
                onBackupNow: {}
            )
        }

        static var autoBackupNeverRunContent: SettingsAutoBackupContent {
            SettingsAutoBackupContent(
                title: "Automatic backup",
                description: "Save your shows, watch history, ratings and settings to a file on a schedule",
                isOn: true,
                locationTitle: "Backup location",
                locationLabel: "tvmaniac-backup.json",
                hasLocation: true,
                scheduleTitle: "How often",
                scheduleOptions: autoBackupScheduleOptions(selected: "WEEKLY"),
                lastRunLabel: "No backup saved yet",
                backupNowTitle: "Back up now",
                backupNowDescription: "Save a backup straight away",
                onToggle: { _ in },
                onBackupNow: {}
            )
        }

        static var autoBackupFailedContent: SettingsAutoBackupContent {
            SettingsAutoBackupContent(
                title: "Automatic backup",
                description: "Save your shows, watch history, ratings and settings to a file on a schedule",
                isOn: true,
                locationTitle: "Backup location",
                locationLabel: "tvmaniac-backup.json",
                hasLocation: true,
                scheduleTitle: "How often",
                scheduleOptions: autoBackupScheduleOptions(selected: "MONTHLY"),
                lastRunLabel: "Last backup 12 August 2026",
                failureWarning: "The last automatic backup failed. Check the location is still available, then back up now.",
                backupNowTitle: "Back up now",
                backupNowDescription: "Save a backup straight away",
                onToggle: { _ in },
                onBackupNow: {}
            )
        }

        static func autoBackupScheduleOptions(selected: String) -> [SettingsAutoBackupScheduleOption] {
            [
                ("DAILY", "Every day"),
                ("WEEKLY", "Every week"),
                ("FORTNIGHTLY", "Every two weeks"),
                ("MONTHLY", "Every month"),
            ].map { id, label in
                SettingsAutoBackupScheduleOption(
                    id: id,
                    label: label,
                    isSelected: id == selected,
                    onSelect: {}
                )
            }
        }

        static var backupContent: SettingsBackupContent {
            SettingsBackupContent(
                exportTitle: "Save a backup",
                exportDescription: "Write your shows, watch history, ratings and settings to a file",
                importTitle: "Restore a backup",
                importDescription: "Replace your shows, watch history, ratings and settings from a file",
                autoBackup: autoBackupContent,
                onExport: {},
                onImport: {}
            )
        }

        static var lockedBackupContent: SettingsBackupContent {
            SettingsBackupContent(
                exportTitle: "Save a backup",
                exportDescription: "Write your shows, watch history, ratings and settings to a file",
                importTitle: "Restore a backup",
                importDescription: "Replace your shows, watch history, ratings and settings from a file",
                autoBackup: autoBackupContent,
                isLocked: true,
                lockedBadgeText: "Premium",
                lockedTitle: "Backup is a Premium feature",
                lockedMessage: "Upgrade to Premium to save and restore your shows.",
                lockedActionText: "Upgrade to Premium",
                lockedAccessibilityLabel: "Locked",
                onExport: {},
                onImport: {},
                onUpgradeClick: {}
            )
        }

        static var exportingBackupContent: SettingsBackupContent {
            SettingsBackupContent(
                exportTitle: "Save a backup",
                exportDescription: "Write your shows, watch history, ratings and settings to a file",
                isExporting: true,
                importTitle: "Restore a backup",
                importDescription: "Replace your shows, watch history, ratings and settings from a file",
                autoBackup: autoBackupContent,
                onExport: {},
                onImport: {}
            )
        }

        static var importingBackupContent: SettingsBackupContent {
            SettingsBackupContent(
                exportTitle: "Save a backup",
                exportDescription: "Write your shows, watch history, ratings and settings to a file",
                importTitle: "Restore a backup",
                importDescription: "Replace your shows, watch history, ratings and settings from a file",
                isImporting: true,
                autoBackup: autoBackupContent,
                onExport: {},
                onImport: {}
            )
        }

        static var autoBackupOnBackupContent: SettingsBackupContent {
            SettingsBackupContent(
                exportTitle: "Save a backup",
                exportDescription: "Write your shows, watch history, ratings and settings to a file",
                importTitle: "Restore a backup",
                importDescription: "Replace your shows, watch history, ratings and settings from a file",
                autoBackup: autoBackupOnContent,
                onExport: {},
                onImport: {}
            )
        }

        static var autoBackupNeverRunBackupContent: SettingsBackupContent {
            SettingsBackupContent(
                exportTitle: "Save a backup",
                exportDescription: "Write your shows, watch history, ratings and settings to a file",
                importTitle: "Restore a backup",
                importDescription: "Replace your shows, watch history, ratings and settings from a file",
                autoBackup: autoBackupNeverRunContent,
                onExport: {},
                onImport: {}
            )
        }

        static var autoBackupFailedBackupContent: SettingsBackupContent {
            SettingsBackupContent(
                exportTitle: "Save a backup",
                exportDescription: "Write your shows, watch history, ratings and settings to a file",
                importTitle: "Restore a backup",
                importDescription: "Replace your shows, watch history, ratings and settings from a file",
                autoBackup: autoBackupFailedContent,
                onExport: {},
                onImport: {}
            )
        }

        static var backupContentWithSummary: SettingsBackupContent {
            SettingsBackupContent(
                exportTitle: "Save a backup",
                exportDescription: "Write your shows, watch history, ratings and settings to a file",
                importTitle: "Restore a backup",
                importDescription: "Replace your shows, watch history, ratings and settings from a file",
                summary: restoreSummaryContent,
                summaryDismissAccessibilityLabel: "Dismiss",
                autoBackup: autoBackupContent,
                onExport: {},
                onImport: {},
                onDismissSummary: {}
            )
        }

        static var backupContentWithSummaryAndSkips: SettingsBackupContent {
            SettingsBackupContent(
                exportTitle: "Save a backup",
                exportDescription: "Write your shows, watch history, ratings and settings to a file",
                importTitle: "Restore a backup",
                importDescription: "Replace your shows, watch history, ratings and settings from a file",
                summary: restoreSummaryContentWithSkips,
                summaryDismissAccessibilityLabel: "Dismiss",
                autoBackup: autoBackupContent,
                onExport: {},
                onImport: {},
                onDismissSummary: {}
            )
        }

        static var restoreSummaryContent: SettingsBackupSummaryContent {
            SettingsBackupSummaryContent(
                title: "Restore complete",
                showsRestored: "48 shows restored",
                episodesRestored: "612 episodes restored"
            )
        }

        static var restoreSummaryContentWithSkips: SettingsBackupSummaryContent {
            SettingsBackupSummaryContent(
                title: "Restore complete",
                showsRestored: "45 shows restored",
                episodesRestored: "598 episodes restored",
                showsSkipped: "3 shows couldn't be restored",
                skippedShows: ["Severance", "The Bear", "Shōgun"],
                rewatchNotice: "Rewatch history wasn't restored."
            )
        }
    }
#endif
