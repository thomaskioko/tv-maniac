import DesignSystem
import SwiftUI
import TvManiacKit

struct SettingsTogglesPageView: View {
    private let toggles: [SettingsToggleItem]

    init(toggles: [SettingsToggleItem]) {
        self.toggles = toggles
    }

    var body: some View {
        SettingsCard {
            ForEach(Array(toggles.enumerated()), id: \.element.id) { index, toggle in
                SettingsToggleRow(toggle)
                    .testTag(toggleTestTag(for: toggle.id))
                if index != toggles.count - 1 {
                    SettingsRowDivider()
                }
            }
        }
    }

    private func toggleTestTag(for id: String) -> String? {
        switch id {
        case "quick-rate": SettingsTestTags.shared.QUICK_RATE_TOGGLE_TEST_TAG
        default: nil
        }
    }
}

#if DEBUG
    #Preview("Behavior") {
        SettingsTogglesPageView(toggles: SettingsPreviewSamples.behaviorToggles)
            .padding()
            .appPreview()
    }

    #Preview("Notifications") {
        SettingsTogglesPageView(toggles: SettingsPreviewSamples.notificationToggles)
            .padding()
            .appPreview()
    }

    #Preview("Notifications Locked") {
        SettingsTogglesPageView(toggles: SettingsPreviewSamples.lockedNotificationToggles)
            .padding()
            .appPreview()
    }
#endif
