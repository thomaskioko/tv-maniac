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
    }
#endif
