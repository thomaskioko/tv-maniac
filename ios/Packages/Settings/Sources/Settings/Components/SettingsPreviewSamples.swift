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
                onAccentColor: TvManiacColorScheme.aqua.onSecondary
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

        static func accountContent(authenticated: Bool) -> SettingsAccountContent {
            SettingsAccountContent(
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
                isProcessingAuth: false,
                logoutLabel: "Logout",
                loginLabel: "Login",
                providerName: "Trakt",
                authProviders: [
                    SwiftAuthProvider(id: "TRAKT", label: "Continue with Trakt", logoName: "TraktMono"),
                    SwiftAuthProvider(id: "SIMKL", label: "Continue with Simkl", logoName: "SimklMono"),
                ],
                onLogout: {},
                onProviderSelected: { _ in }
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

        private static func navItem(_ route: SettingsPageRoute, _ title: String, _ subtitle: String) -> SettingsNavigationItem {
            SettingsNavigationItem(id: route.rawValue, icon: route.iconName, title: title, subtitle: subtitle, onTap: {})
        }
    }
#endif
