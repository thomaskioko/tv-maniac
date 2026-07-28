#if DEBUG
    import Components
    import DesignSystem
    import Models
    import SwiftUI

    extension SettingsPreviewSamples {
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
