import Components
import Foundation
import Models
import SwiftUI

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

public struct SettingsBackupContent {
    public let exportTitle: String
    public let exportDescription: String
    public let isExporting: Bool
    public let importTitle: String
    public let importDescription: String
    public let isImporting: Bool
    public let summary: SettingsBackupSummaryContent?
    public let summaryDismissAccessibilityLabel: String
    public let isLocked: Bool
    public let lockedBadgeText: String
    public let lockedTitle: String
    public let lockedMessage: String
    public let lockedActionText: String
    public let lockedAccessibilityLabel: String
    public let onExport: () -> Void
    public let onImport: () -> Void
    public let onUpgradeClick: () -> Void
    public let onDismissSummary: () -> Void

    public init(
        exportTitle: String,
        exportDescription: String,
        isExporting: Bool = false,
        importTitle: String,
        importDescription: String,
        isImporting: Bool = false,
        summary: SettingsBackupSummaryContent? = nil,
        summaryDismissAccessibilityLabel: String = "",
        isLocked: Bool = false,
        lockedBadgeText: String = "",
        lockedTitle: String = "",
        lockedMessage: String = "",
        lockedActionText: String = "",
        lockedAccessibilityLabel: String = "",
        onExport: @escaping () -> Void,
        onImport: @escaping () -> Void,
        onUpgradeClick: @escaping () -> Void = {},
        onDismissSummary: @escaping () -> Void = {}
    ) {
        self.exportTitle = exportTitle
        self.exportDescription = exportDescription
        self.isExporting = isExporting
        self.importTitle = importTitle
        self.importDescription = importDescription
        self.isImporting = isImporting
        self.summary = summary
        self.summaryDismissAccessibilityLabel = summaryDismissAccessibilityLabel
        self.isLocked = isLocked
        self.lockedBadgeText = lockedBadgeText
        self.lockedTitle = lockedTitle
        self.lockedMessage = lockedMessage
        self.lockedActionText = lockedActionText
        self.lockedAccessibilityLabel = lockedAccessibilityLabel
        self.onExport = onExport
        self.onImport = onImport
        self.onUpgradeClick = onUpgradeClick
        self.onDismissSummary = onDismissSummary
    }
}

public struct SettingsBackupSummaryContent {
    public let title: String
    public let showsRestored: String
    public let episodesRestored: String
    public let showsSkipped: String?
    public let skippedShows: [String]
    public let rewatchNotice: String?

    public init(
        title: String,
        showsRestored: String,
        episodesRestored: String,
        showsSkipped: String? = nil,
        skippedShows: [String] = [],
        rewatchNotice: String? = nil
    ) {
        self.title = title
        self.showsRestored = showsRestored
        self.episodesRestored = episodesRestored
        self.showsSkipped = showsSkipped
        self.skippedShows = skippedShows
        self.rewatchNotice = rewatchNotice
    }
}
