import Components
import Foundation
import Models
import SwiftUI

public struct SettingsToggleItem: Identifiable {
    public let id: String
    public let icon: String
    public let title: String
    public let subtitle: String
    public let secondarySubtitle: String?
    public let isOn: Bool
    public let isLocked: Bool
    public let lockedBadgeText: String
    public let lockedAccessibilityLabel: String
    public let onToggle: (Bool) -> Void

    public init(
        id: String,
        icon: String,
        title: String,
        subtitle: String,
        secondarySubtitle: String? = nil,
        isOn: Bool,
        isLocked: Bool = false,
        lockedBadgeText: String = "",
        lockedAccessibilityLabel: String = "",
        onToggle: @escaping (Bool) -> Void
    ) {
        self.id = id
        self.icon = icon
        self.title = title
        self.subtitle = subtitle
        self.secondarySubtitle = secondarySubtitle
        self.isOn = isOn
        self.isLocked = isLocked
        self.lockedBadgeText = lockedBadgeText
        self.lockedAccessibilityLabel = lockedAccessibilityLabel
        self.onToggle = onToggle
    }
}

extension SettingsToggleItem: Equatable {
    public static func == (lhs: SettingsToggleItem, rhs: SettingsToggleItem) -> Bool {
        lhs.id == rhs.id
            && lhs.icon == rhs.icon
            && lhs.title == rhs.title
            && lhs.subtitle == rhs.subtitle
            && lhs.secondarySubtitle == rhs.secondarySubtitle
            && lhs.isOn == rhs.isOn
            && lhs.isLocked == rhs.isLocked
    }
}

public struct SettingsNavigationItem: Identifiable {
    public let id: String
    public let icon: String
    public let title: String
    public let subtitle: String?
    public let onTap: () -> Void

    public init(
        id: String,
        icon: String,
        title: String,
        subtitle: String? = nil,
        onTap: @escaping () -> Void
    ) {
        self.id = id
        self.icon = icon
        self.title = title
        self.subtitle = subtitle
        self.onTap = onTap
    }
}

extension SettingsNavigationItem: Equatable {
    public static func == (lhs: SettingsNavigationItem, rhs: SettingsNavigationItem) -> Bool {
        lhs.id == rhs.id
            && lhs.icon == rhs.icon
            && lhs.title == rhs.title
            && lhs.subtitle == rhs.subtitle
    }
}

public struct SettingsImageQualityItem: Equatable {
    public let icon: String
    public let title: String
    public let subtitle: String
    public let options: [SettingsImageQualityOption]
    public let selectedOptionId: String

    public init(
        icon: String,
        title: String,
        subtitle: String,
        options: [SettingsImageQualityOption],
        selectedOptionId: String
    ) {
        self.icon = icon
        self.title = title
        self.subtitle = subtitle
        self.options = options
        self.selectedOptionId = selectedOptionId
    }
}

public struct SettingsImageQualityOption: Identifiable, Equatable {
    public let id: String
    public let label: String
    public let onSelect: () -> Void

    public init(id: String, label: String, onSelect: @escaping () -> Void) {
        self.id = id
        self.label = label
        self.onSelect = onSelect
    }

    public static func == (lhs: SettingsImageQualityOption, rhs: SettingsImageQualityOption) -> Bool {
        lhs.id == rhs.id && lhs.label == rhs.label
    }
}

public struct SettingsThemeItem<Theme: ThemeItem>: Equatable {
    public let icon: String
    public let title: String
    public let subtitle: String
    public let themes: [Theme]
    public let selectedTheme: Theme
    public let isCustomThemesLocked: Bool
    public let lockedBadgeText: String
    public let lockedTitle: String
    public let lockedMessage: String
    public let lockedActionText: String
    public let lockedAccessibilityLabel: String
    public let onUpgradeClick: () -> Void
    public let onThemeSelected: (Theme) -> Void

    public init(
        icon: String,
        title: String,
        subtitle: String,
        themes: [Theme],
        selectedTheme: Theme,
        isCustomThemesLocked: Bool = false,
        lockedBadgeText: String = "",
        lockedTitle: String = "",
        lockedMessage: String = "",
        lockedActionText: String = "",
        lockedAccessibilityLabel: String = "",
        onUpgradeClick: @escaping () -> Void = {},
        onThemeSelected: @escaping (Theme) -> Void
    ) {
        self.icon = icon
        self.title = title
        self.subtitle = subtitle
        self.themes = themes
        self.selectedTheme = selectedTheme
        self.isCustomThemesLocked = isCustomThemesLocked
        self.lockedBadgeText = lockedBadgeText
        self.lockedTitle = lockedTitle
        self.lockedMessage = lockedMessage
        self.lockedActionText = lockedActionText
        self.lockedAccessibilityLabel = lockedAccessibilityLabel
        self.onUpgradeClick = onUpgradeClick
        self.onThemeSelected = onThemeSelected
    }

    public static func == (lhs: SettingsThemeItem, rhs: SettingsThemeItem) -> Bool {
        lhs.icon == rhs.icon
            && lhs.title == rhs.title
            && lhs.subtitle == rhs.subtitle
            && lhs.selectedTheme.id == rhs.selectedTheme.id
            && lhs.isCustomThemesLocked == rhs.isCustomThemesLocked
    }
}

public struct SettingsFontSizeItem: Equatable {
    public let title: String
    public let description: String
    public let previewText: String
    public let resetLabel: String
    public let percent: Int
    public let onPercentChange: (Int) -> Void

    public init(
        title: String,
        description: String,
        previewText: String,
        resetLabel: String,
        percent: Int,
        onPercentChange: @escaping (Int) -> Void
    ) {
        self.title = title
        self.description = description
        self.previewText = previewText
        self.resetLabel = resetLabel
        self.percent = percent
        self.onPercentChange = onPercentChange
    }

    public static func == (lhs: SettingsFontSizeItem, rhs: SettingsFontSizeItem) -> Bool {
        lhs.title == rhs.title
            && lhs.description == rhs.description
            && lhs.previewText == rhs.previewText
            && lhs.resetLabel == rhs.resetLabel
            && lhs.percent == rhs.percent
    }
}

public struct SettingsRootSection: Identifiable {
    public let id: String
    public let label: String
    public let items: [SettingsNavigationItem]

    public init(id: String, label: String, items: [SettingsNavigationItem]) {
        self.id = id
        self.label = label
        self.items = items
    }
}

public struct SettingsLinkItem: Identifiable {
    public let id: String
    public let leadingSystemImage: String?
    public let leadingAsset: String?
    public let title: String
    public let body: String
    public let link: String
    public let onOpen: () -> Void

    public init(
        id: String,
        leadingSystemImage: String? = nil,
        leadingAsset: String? = nil,
        title: String,
        body: String,
        link: String,
        onOpen: @escaping () -> Void
    ) {
        self.id = id
        self.leadingSystemImage = leadingSystemImage
        self.leadingAsset = leadingAsset
        self.title = title
        self.body = body
        self.link = link
        self.onOpen = onOpen
    }
}

public struct SettingsInfoContent {
    public let icon: Image
    public let appName: String
    public let versionText: String
    public let description: String
    public let sourceCodeLabel: String
    public let sourceCodeValue: String
    public let apiDisclaimer: String
    public let onVersionTap: () -> Void
    public let onSourceCodeTap: () -> Void

    public init(
        icon: Image,
        appName: String,
        versionText: String,
        description: String,
        sourceCodeLabel: String,
        sourceCodeValue: String,
        apiDisclaimer: String,
        onVersionTap: @escaping () -> Void,
        onSourceCodeTap: @escaping () -> Void
    ) {
        self.icon = icon
        self.appName = appName
        self.versionText = versionText
        self.description = description
        self.sourceCodeLabel = sourceCodeLabel
        self.sourceCodeValue = sourceCodeValue
        self.apiDisclaimer = apiDisclaimer
        self.onVersionTap = onVersionTap
        self.onSourceCodeTap = onSourceCodeTap
    }
}

public struct SettingsLicenseSection: Identifiable {
    public let id: String
    public let label: String
    public let items: [SettingsLinkItem]

    public init(id: String, label: String, items: [SettingsLinkItem]) {
        self.id = id
        self.label = label
        self.items = items
    }
}

public struct SettingsAccountContent {
    public let title: String
    public let description: String
    public let authenticationLabel: String
    public let connectTitle: String
    public let syncDescription: String
    public let connectedTitle: String
    public let connectedDescription: String
    public let isAuthenticated: Bool
    public let isProcessingAuth: Bool
    public let logoutLabel: String
    public let loginLabel: String
    public let providerName: String
    public let providerLogoName: String
    public let authProviders: [SwiftAuthProvider]
    public let switchTargetLogoName: String?
    public let switchActionLabel: String?
    public let isSwitching: Bool
    public let showSwitchConfirmation: Bool
    public let switchDialogTitle: String?
    public let switchDialogMessage: String?
    public let switchConfirmLabel: String
    public let switchCancelLabel: String
    public let switchingLabel: String
    public let onLogout: () -> Void
    public let onLogin: () -> Void
    public let onProviderSelected: (String) -> Void
    public let onSwitchProvider: () -> Void
    public let onConfirmSwitch: () -> Void
    public let onDismissSwitchDialog: () -> Void

    public init(
        title: String,
        description: String,
        authenticationLabel: String,
        connectTitle: String = "",
        syncDescription: String = "",
        connectedTitle: String,
        connectedDescription: String,
        isAuthenticated: Bool,
        isProcessingAuth: Bool,
        logoutLabel: String,
        loginLabel: String,
        providerName: String = "",
        providerLogoName: String = "TraktMono",
        authProviders: [SwiftAuthProvider] = [],
        switchTargetLogoName: String? = nil,
        switchActionLabel: String? = nil,
        isSwitching: Bool = false,
        showSwitchConfirmation: Bool = false,
        switchDialogTitle: String? = nil,
        switchDialogMessage: String? = nil,
        switchConfirmLabel: String = "",
        switchCancelLabel: String = "",
        switchingLabel: String = "",
        onLogout: @escaping () -> Void,
        onLogin: @escaping () -> Void = {},
        onProviderSelected: @escaping (String) -> Void = { _ in },
        onSwitchProvider: @escaping () -> Void = {},
        onConfirmSwitch: @escaping () -> Void = {},
        onDismissSwitchDialog: @escaping () -> Void = {}
    ) {
        self.title = title
        self.description = description
        self.authenticationLabel = authenticationLabel
        self.connectTitle = connectTitle
        self.syncDescription = syncDescription
        self.connectedTitle = connectedTitle
        self.connectedDescription = connectedDescription
        self.isAuthenticated = isAuthenticated
        self.isProcessingAuth = isProcessingAuth
        self.logoutLabel = logoutLabel
        self.loginLabel = loginLabel
        self.providerName = providerName
        self.providerLogoName = providerLogoName
        self.authProviders = authProviders
        self.switchTargetLogoName = switchTargetLogoName
        self.switchActionLabel = switchActionLabel
        self.isSwitching = isSwitching
        self.showSwitchConfirmation = showSwitchConfirmation
        self.switchDialogTitle = switchDialogTitle
        self.switchDialogMessage = switchDialogMessage
        self.switchConfirmLabel = switchConfirmLabel
        self.switchCancelLabel = switchCancelLabel
        self.switchingLabel = switchingLabel
        self.onLogout = onLogout
        self.onLogin = onLogin
        self.onProviderSelected = onProviderSelected
        self.onSwitchProvider = onSwitchProvider
        self.onConfirmSwitch = onConfirmSwitch
        self.onDismissSwitchDialog = onDismissSwitchDialog
    }
}
