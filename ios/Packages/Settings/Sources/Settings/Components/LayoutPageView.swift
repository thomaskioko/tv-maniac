import DesignSystem
import SwiftUI

struct LayoutPageView: View {
    @Environment(\.appTheme) private var appTheme
    private let toggles: [SettingsToggleItem]
    private let discoverSectionsItem: SettingsNavigationItem
    private let fontSizeItem: SettingsFontSizeItem

    init(
        toggles: [SettingsToggleItem],
        discoverSectionsItem: SettingsNavigationItem,
        fontSizeItem: SettingsFontSizeItem
    ) {
        self.toggles = toggles
        self.discoverSectionsItem = discoverSectionsItem
        self.fontSizeItem = fontSizeItem
    }

    var body: some View {
        VStack(alignment: .leading, spacing: appTheme.spacing.medium) {
            SettingsTogglesPageView(toggles: toggles)
            SettingsCard {
                SettingsFontSizeRow(fontSizeItem)
            }
            SettingsCard {
                SettingsNavigationRow(discoverSectionsItem)
            }
        }
    }
}

#if DEBUG
    #Preview {
        LayoutPageView(
            toggles: SettingsPreviewSamples.layoutToggles,
            discoverSectionsItem: SettingsPreviewSamples.discoverSectionsNavItem,
            fontSizeItem: SettingsPreviewSamples.fontSizeItem
        )
        .padding()
        .appPreview()
    }
#endif
