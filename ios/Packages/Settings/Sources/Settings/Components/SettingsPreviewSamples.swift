#if DEBUG
    import Components
    import DesignSystem
    import Models
    import SwiftUI

    enum SettingsPreviewSamples {
        static let themes: [ThemeItemModel] = [
            ThemeItemModel(
                id: "system",
                displayName: "System",
                backgroundColor: TvManiacColorScheme.light.background,
                accentColor: TvManiacColorScheme.light.secondary,
                onAccentColor: TvManiacColorScheme.light.onSecondary,
                isSystemTheme: true
            ),
            ThemeItemModel(
                id: "light",
                displayName: "Light",
                backgroundColor: TvManiacColorScheme.light.background,
                accentColor: TvManiacColorScheme.light.secondary,
                onAccentColor: TvManiacColorScheme.light.onSecondary
            ),
            ThemeItemModel(
                id: "dark",
                displayName: "Dark",
                backgroundColor: TvManiacColorScheme.dark.background,
                accentColor: TvManiacColorScheme.dark.secondary,
                onAccentColor: TvManiacColorScheme.dark.onSecondary
            ),
            ThemeItemModel(
                id: "aqua",
                displayName: "Aqua",
                backgroundColor: TvManiacColorScheme.aqua.background,
                accentColor: TvManiacColorScheme.aqua.secondary,
                onAccentColor: TvManiacColorScheme.aqua.onSecondary,
                isPremium: true
            ),
        ]

        static var themeItem: SettingsThemeItem<ThemeItemModel> {
            SettingsThemeItem(
                icon: "paintpalette",
                title: "Theme",
                subtitle: "Choose your preferred theme",
                themes: themes,
                selectedTheme: themes[0],
                onThemeSelected: { _ in }
            )
        }

        static var customThemeItem: SettingsThemeItem<ThemeItemModel> {
            SettingsThemeItem(
                icon: "paintpalette",
                title: "Theme",
                subtitle: "Choose your preferred theme",
                themes: themes,
                selectedTheme: themes[0],
                isCustomThemesLocked: true,
                lockedBadgeText: "Premium",
                lockedTitle: "Custom themes are a Premium feature",
                lockedMessage: "Upgrade to Premium to use custom themes.",
                lockedActionText: "Upgrade to Premium",
                lockedAccessibilityLabel: "Locked",
                onThemeSelected: { _ in }
            )
        }

        static var imageQualityItem: SettingsImageQualityItem {
            SettingsImageQualityItem(
                icon: "photo",
                title: "Image Quality",
                subtitle: "Automatically adjusts based on network",
                options: [
                    SettingsImageQualityOption(id: "AUTO", label: "Auto", onSelect: {}),
                    SettingsImageQualityOption(id: "HIGH", label: "High", onSelect: {}),
                    SettingsImageQualityOption(id: "MEDIUM", label: "Medium", onSelect: {}),
                    SettingsImageQualityOption(id: "LOW", label: "Low", onSelect: {}),
                ],
                selectedOptionId: "AUTO"
            )
        }

        static var layoutToggles: [SettingsToggleItem] {
            [
                SettingsToggleItem(
                    id: "haptic",
                    icon: "iphone.radiowaves.left.and.right",
                    title: "Haptic feedback",
                    subtitle: "Feel subtle vibrations during interactions",
                    isOn: true,
                    onToggle: { _ in }
                ),
                SettingsToggleItem(
                    id: "season-order",
                    icon: "arrow.up.arrow.down",
                    title: "Season Order",
                    subtitle: "Order the latest season first",
                    isOn: false,
                    onToggle: { _ in }
                ),
                SettingsToggleItem(
                    id: "blur-unwatched",
                    icon: "eye.slash",
                    title: "Hide Spoilers",
                    subtitle: "Hide spoilers for unwatched episodes",
                    isOn: false,
                    onToggle: { _ in }
                ),
            ]
        }

        static var fontSizeItem: SettingsFontSizeItem {
            SettingsFontSizeItem(
                title: "Font Size",
                description: "Adjust text size across the app",
                previewText: "The quick brown fox jumps over the lazy dog",
                resetLabel: "Reset",
                percent: 100,
                onPercentChange: { _ in }
            )
        }

        static var posterStyleNavItem: SettingsNavigationItem {
            SettingsNavigationItem(
                id: SettingsPageRoute.posterStyle.rawValue,
                icon: SettingsPageRoute.posterStyle.iconName,
                title: "Poster style",
                subtitle: "Choose poster size and corner style",
                onTap: {}
            )
        }

        static var posterStyleItem: SettingsPosterStyleItem {
            SettingsPosterStyleItem(
                title: "Poster style",
                description: "Choose poster size and corner style",
                livePreviewLabel: "Live preview",
                resetLabel: "Reset",
                postersLabel: "Posters",
                landscapeLabel: "Landscape",
                cornerLabel: "Corner style",
                postersOptions: posterWidthOptions,
                landscapeOptions: posterWidthOptions,
                cornerOptions: posterCornerOptions,
                selectedPostersId: "STANDARD",
                selectedLandscapeId: "STANDARD",
                selectedCornerId: "SHARP",
                posterScale: 1,
                landscapeScale: 1,
                cornerRadius: 0
            )
        }

        static var lockedPosterStyleItem: SettingsPosterStyleItem {
            SettingsPosterStyleItem(
                title: "Poster style",
                description: "Choose poster size and corner style",
                livePreviewLabel: "Live preview",
                resetLabel: "Reset",
                postersLabel: "Posters",
                landscapeLabel: "Landscape",
                cornerLabel: "Corner style",
                postersOptions: posterWidthOptions,
                landscapeOptions: posterWidthOptions,
                cornerOptions: posterCornerOptions,
                selectedPostersId: "STANDARD",
                selectedLandscapeId: "STANDARD",
                selectedCornerId: "SHARP",
                posterScale: 1,
                landscapeScale: 1,
                cornerRadius: 0,
                isLocked: true,
                lockedBadgeText: "Premium",
                lockedActionText: "Upgrade to Premium",
                lockedAccessibilityLabel: "Locked"
            )
        }

        private static var posterWidthOptions: [SettingsPosterStyleOption] {
            [
                SettingsPosterStyleOption(id: "COMPACT", label: "Compact", onSelect: {}),
                SettingsPosterStyleOption(id: "STANDARD", label: "Standard", onSelect: {}),
                SettingsPosterStyleOption(id: "COMFORTABLE", label: "Comfortable", onSelect: {}),
                SettingsPosterStyleOption(id: "LARGE", label: "Large", onSelect: {}),
            ]
        }

        private static var posterCornerOptions: [SettingsPosterStyleOption] {
            [
                SettingsPosterStyleOption(id: "SHARP", label: "Sharp", onSelect: {}),
                SettingsPosterStyleOption(id: "CLASSIC", label: "Classic", onSelect: {}),
                SettingsPosterStyleOption(id: "ROUNDED", label: "Rounded", onSelect: {}),
                SettingsPosterStyleOption(id: "PILL", label: "Pill", onSelect: {}),
            ]
        }

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

        static var infoContent: SettingsInfoContent {
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

        static var licenseSections: [SettingsLicenseSection] {
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

        static func accountContent(
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
                    :
                    "Sign in with Trakt to sync your watch history, watchlist, and episode progress across your devices.",
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

        static func rootSections(authenticated: Bool) -> [SettingsRootSection] {
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

        private static func navItem(_ route: SettingsPageRoute, _ title: String,
                                    _ subtitle: String) -> SettingsNavigationItem
        {
            SettingsNavigationItem(
                id: route.rawValue,
                icon: route.iconName,
                title: title,
                subtitle: subtitle,
                onTap: {}
            )
        }
    }
#endif
