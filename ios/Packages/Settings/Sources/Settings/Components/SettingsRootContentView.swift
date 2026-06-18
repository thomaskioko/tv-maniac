import DesignSystem
import SwiftUI

struct SettingsRootContentView: View {
    @Environment(\.appTheme) private var appTheme
    private let sections: [SettingsRootSection]

    init(sections: [SettingsRootSection]) {
        self.sections = sections
    }

    var body: some View {
        VStack(alignment: .leading, spacing: appTheme.spacing.large) {
            ForEach(sections) { section in
                VStack(alignment: .leading, spacing: appTheme.spacing.small) {
                    SettingsSectionLabel(section.label)
                    SettingsCard {
                        ForEach(Array(section.items.enumerated()), id: \.element.id) { index, item in
                            SettingsNavigationRow(item)
                            if index != section.items.count - 1 {
                                SettingsRowDivider()
                            }
                        }
                    }
                }
            }
        }
    }
}

#if DEBUG
    #Preview {
        SettingsRootContentView(
            sections: SettingsPreviewSamples.rootSections(authenticated: true)
        )
        .padding()
        .appPreview()
    }
#endif
