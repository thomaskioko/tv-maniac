import DesignSystem
import SwiftUI

struct LayoutPageView: View {
    @Environment(\.appTheme) private var appTheme
    private let toggles: [SettingsToggleItem]
    private let discoverSectionsItem: SettingsNavigationItem

    init(toggles: [SettingsToggleItem], discoverSectionsItem: SettingsNavigationItem) {
        self.toggles = toggles
        self.discoverSectionsItem = discoverSectionsItem
    }

    var body: some View {
        VStack(alignment: .leading, spacing: appTheme.spacing.medium) {
            SettingsTogglesPageView(toggles: toggles)
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
            discoverSectionsItem: SettingsPreviewSamples.discoverSectionsNavItem
        )
        .padding()
        .appPreview()
    }
#endif
