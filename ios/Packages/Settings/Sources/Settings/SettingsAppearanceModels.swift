import Components
import DesignSystem
import Foundation
import Models
import SwiftUI

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

public struct SettingsPosterStyleOption: Identifiable, Equatable {
    public let id: String
    public let label: String
    public let onSelect: () -> Void

    public init(id: String, label: String, onSelect: @escaping () -> Void) {
        self.id = id
        self.label = label
        self.onSelect = onSelect
    }

    public static func == (lhs: SettingsPosterStyleOption, rhs: SettingsPosterStyleOption) -> Bool {
        lhs.id == rhs.id && lhs.label == rhs.label
    }
}

public struct SettingsPosterStyleItem: Equatable {
    public let title: String
    public let description: String
    public let livePreviewLabel: String
    public let resetLabel: String
    public let postersLabel: String
    public let landscapeLabel: String
    public let cornerLabel: String
    public let postersOptions: [SettingsPosterStyleOption]
    public let landscapeOptions: [SettingsPosterStyleOption]
    public let cornerOptions: [SettingsPosterStyleOption]
    public let selectedPostersId: String
    public let selectedLandscapeId: String
    public let selectedCornerId: String
    public let posterScale: CGFloat
    public let landscapeScale: CGFloat
    public let cornerRadius: CGFloat
    public let isLocked: Bool
    public let lockedBadgeText: String
    public let lockedActionText: String
    public let lockedAccessibilityLabel: String
    public let onUpgradeClick: () -> Void
    public let onReset: () -> Void

    public init(
        title: String,
        description: String,
        livePreviewLabel: String,
        resetLabel: String,
        postersLabel: String,
        landscapeLabel: String,
        cornerLabel: String,
        postersOptions: [SettingsPosterStyleOption],
        landscapeOptions: [SettingsPosterStyleOption],
        cornerOptions: [SettingsPosterStyleOption],
        selectedPostersId: String,
        selectedLandscapeId: String,
        selectedCornerId: String,
        posterScale: CGFloat,
        landscapeScale: CGFloat,
        cornerRadius: CGFloat,
        isLocked: Bool = false,
        lockedBadgeText: String = "",
        lockedActionText: String = "",
        lockedAccessibilityLabel: String = "",
        onUpgradeClick: @escaping () -> Void = {},
        onReset: @escaping () -> Void = {}
    ) {
        self.title = title
        self.description = description
        self.livePreviewLabel = livePreviewLabel
        self.resetLabel = resetLabel
        self.postersLabel = postersLabel
        self.landscapeLabel = landscapeLabel
        self.cornerLabel = cornerLabel
        self.postersOptions = postersOptions
        self.landscapeOptions = landscapeOptions
        self.cornerOptions = cornerOptions
        self.selectedPostersId = selectedPostersId
        self.selectedLandscapeId = selectedLandscapeId
        self.selectedCornerId = selectedCornerId
        self.posterScale = posterScale
        self.landscapeScale = landscapeScale
        self.cornerRadius = cornerRadius
        self.isLocked = isLocked
        self.lockedBadgeText = lockedBadgeText
        self.lockedActionText = lockedActionText
        self.lockedAccessibilityLabel = lockedAccessibilityLabel
        self.onUpgradeClick = onUpgradeClick
        self.onReset = onReset
    }

    public static func == (lhs: SettingsPosterStyleItem, rhs: SettingsPosterStyleItem) -> Bool {
        lhs.title == rhs.title
            && lhs.description == rhs.description
            && lhs.selectedPostersId == rhs.selectedPostersId
            && lhs.selectedLandscapeId == rhs.selectedLandscapeId
            && lhs.selectedCornerId == rhs.selectedCornerId
            && lhs.posterScale == rhs.posterScale
            && lhs.landscapeScale == rhs.landscapeScale
            && lhs.cornerRadius == rhs.cornerRadius
            && lhs.isLocked == rhs.isLocked
    }
}

public struct SettingsWidgetThemeOption: Identifiable, Equatable {
    public let id: String
    public let label: String
    public let onSelect: () -> Void

    public init(id: String, label: String, onSelect: @escaping () -> Void) {
        self.id = id
        self.label = label
        self.onSelect = onSelect
    }

    public static func == (lhs: SettingsWidgetThemeOption, rhs: SettingsWidgetThemeOption) -> Bool {
        lhs.id == rhs.id && lhs.label == rhs.label
    }
}

public struct SettingsWidgetAppearanceItem: Equatable {
    public let description: String
    public let livePreviewLabel: String
    public let themeLabel: String
    public let options: [SettingsWidgetThemeOption]
    public let selectedOptionId: String
    public let previewTheme: TvManiacTheme
    public let isLocked: Bool
    public let lockedBadgeText: String
    public let lockedActionText: String
    public let lockedAccessibilityLabel: String
    public let onUpgradeClick: () -> Void

    public init(
        description: String,
        livePreviewLabel: String,
        themeLabel: String,
        options: [SettingsWidgetThemeOption],
        selectedOptionId: String,
        previewTheme: TvManiacTheme,
        isLocked: Bool = false,
        lockedBadgeText: String = "",
        lockedActionText: String = "",
        lockedAccessibilityLabel: String = "",
        onUpgradeClick: @escaping () -> Void = {}
    ) {
        self.description = description
        self.livePreviewLabel = livePreviewLabel
        self.themeLabel = themeLabel
        self.options = options
        self.selectedOptionId = selectedOptionId
        self.previewTheme = previewTheme
        self.isLocked = isLocked
        self.lockedBadgeText = lockedBadgeText
        self.lockedActionText = lockedActionText
        self.lockedAccessibilityLabel = lockedAccessibilityLabel
        self.onUpgradeClick = onUpgradeClick
    }

    public static func == (lhs: SettingsWidgetAppearanceItem, rhs: SettingsWidgetAppearanceItem) -> Bool {
        lhs.description == rhs.description
            && lhs.selectedOptionId == rhs.selectedOptionId
            && lhs.options == rhs.options
            && lhs.isLocked == rhs.isLocked
    }
}
