import DesignSystem
import SwiftUI

struct PrivacyPageView: View {
    private let toggles: [SettingsToggleItem]
    private let links: [SettingsNavigationItem]

    init(toggles: [SettingsToggleItem], links: [SettingsNavigationItem]) {
        self.toggles = toggles
        self.links = links
    }

    var body: some View {
        SettingsCard {
            ForEach(toggles) { toggle in
                SettingsToggleRow(toggle)
            }
            ForEach(links) { item in
                SettingsRowDivider()
                SettingsNavigationRow(item)
            }
        }
    }
}

#if DEBUG
    #Preview {
        PrivacyPageView(
            toggles: SettingsPreviewSamples.privacyToggles,
            links: SettingsPreviewSamples.privacyLinks
        )
        .padding()
        .appPreview()
    }
#endif
