import DesignSystem
import SwiftUI

struct LayoutPageView: View {
    @Environment(\.appTheme) private var appTheme
    private let toggles: [SettingsToggleItem]
    private let discoverSectionsItem: SettingsNavigationItem
    private let fontSizeItem: SettingsFontSizeItem
    private let posterStyleItem: SettingsNavigationItem

    init(
        toggles: [SettingsToggleItem],
        discoverSectionsItem: SettingsNavigationItem,
        fontSizeItem: SettingsFontSizeItem,
        posterStyleItem: SettingsNavigationItem
    ) {
        self.toggles = toggles
        self.discoverSectionsItem = discoverSectionsItem
        self.fontSizeItem = fontSizeItem
        self.posterStyleItem = posterStyleItem
    }

    var body: some View {
        VStack(alignment: .leading, spacing: appTheme.spacing.medium) {
            SettingsTogglesPageView(toggles: toggles)
            SettingsCard {
                SettingsNavigationRow(discoverSectionsItem)
                SettingsRowDivider()
                SettingsNavigationRow(posterStyleItem)
            }
            SettingsCard {
                SettingsFontSizeRow(fontSizeItem)
            }
        }
    }
}

#if DEBUG
    #Preview {
        LayoutPageView(
            toggles: SettingsPreviewSamples.layoutToggles,
            discoverSectionsItem: SettingsPreviewSamples.discoverSectionsNavItem,
            fontSizeItem: SettingsPreviewSamples.fontSizeItem,
            posterStyleItem: SettingsPreviewSamples.posterStyleNavItem
        )
        .padding()
        .appPreview()
    }
#endif
