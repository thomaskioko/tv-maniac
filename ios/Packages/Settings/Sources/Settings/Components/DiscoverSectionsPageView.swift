import DesignSystem
import SwiftUI

struct DiscoverSectionsPageView: View {
    private let toggles: [SettingsToggleItem]

    init(toggles: [SettingsToggleItem]) {
        self.toggles = toggles
    }

    var body: some View {
        SettingsTogglesPageView(toggles: toggles)
    }
}

#if DEBUG
    #Preview {
        DiscoverSectionsPageView(toggles: SettingsPreviewSamples.discoverSectionToggles)
            .padding()
            .appPreview()
    }
#endif
