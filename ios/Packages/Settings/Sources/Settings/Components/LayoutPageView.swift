import DesignSystem
import SwiftUI

struct LayoutPageView: View {
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
        LayoutPageView(toggles: SettingsPreviewSamples.layoutToggles)
            .padding()
            .appPreview()
    }
#endif
