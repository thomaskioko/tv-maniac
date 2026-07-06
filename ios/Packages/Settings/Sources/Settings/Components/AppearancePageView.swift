import Components
import DesignSystem
import SwiftUI

struct AppearancePageView<Theme: ThemeItem>: View {
    @Environment(\.appTheme) private var appTheme
    private let themeItem: SettingsThemeItem<Theme>
    private let imageQualityItem: SettingsImageQualityItem

    init(themeItem: SettingsThemeItem<Theme>, imageQualityItem: SettingsImageQualityItem) {
        self.themeItem = themeItem
        self.imageQualityItem = imageQualityItem
    }

    var body: some View {
        VStack(alignment: .leading, spacing: appTheme.spacing.large) {
            VStack(alignment: .leading, spacing: appTheme.spacing.small) {
                SettingsSectionLabel(themeItem.title)
                SettingsCard {
                    ThemeSelectorView(
                        themes: themeItem.themes,
                        selectedTheme: themeItem.selectedTheme,
                        isCustomThemesLocked: themeItem.isCustomThemesLocked,
                        lockedBadgeText: themeItem.lockedBadgeText,
                        lockedTitle: themeItem.lockedTitle,
                        lockedMessage: themeItem.lockedMessage,
                        lockedActionText: themeItem.lockedActionText,
                        lockedAccessibilityLabel: themeItem.lockedAccessibilityLabel,
                        onUpgradeClick: themeItem.onUpgradeClick,
                        onThemeSelected: themeItem.onThemeSelected
                    )
                    .padding(appTheme.spacing.medium)
                }
            }

            VStack(alignment: .leading, spacing: appTheme.spacing.small) {
                SettingsSectionLabel(imageQualityItem.title)
                SettingsCard {
                    VStack(alignment: .leading, spacing: appTheme.spacing.small) {
                        HStack(spacing: appTheme.spacing.small) {
                            ForEach(imageQualityItem.options) { option in
                                SelectionChip(
                                    label: option.label,
                                    isSelected: option.id == imageQualityItem.selectedOptionId,
                                    action: option.onSelect
                                )
                            }
                        }
                        Text(imageQualityItem.subtitle)
                            .textStyle(appTheme.typography.bodySmall)
                            .foregroundColor(appTheme.colors.onSurfaceVariant)
                    }
                    .padding(appTheme.spacing.medium)
                }
            }
        }
    }
}

#if DEBUG
    #Preview {
        AppearancePageView(
            themeItem: SettingsPreviewSamples.themeItem,
            imageQualityItem: SettingsPreviewSamples.imageQualityItem
        )
        .padding()
        .appPreview()
    }

    #Preview("Locked") {
        AppearancePageView(
            themeItem: SettingsPreviewSamples.customThemeItem,
            imageQualityItem: SettingsPreviewSamples.imageQualityItem
        )
        .padding()
        .appPreview()
    }
#endif
