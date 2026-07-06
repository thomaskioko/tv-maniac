import DesignSystem
import SwiftUI

struct SettingsTogglesPageView: View {
    private let toggles: [SettingsToggleItem]

    init(toggles: [SettingsToggleItem]) {
        self.toggles = toggles
    }

    var body: some View {
        SettingsCard {
            ForEach(Array(toggles.enumerated()), id: \.element.id) { index, toggle in
                SettingsToggleRow(toggle)
                if index != toggles.count - 1 {
                    SettingsRowDivider()
                }
            }
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
