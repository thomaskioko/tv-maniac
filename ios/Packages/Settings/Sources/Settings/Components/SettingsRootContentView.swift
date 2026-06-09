import DesignSystem
import SwiftUI

struct SettingsRootContentView: View {
    @Environment(\.appTheme) private var appTheme
    private let sections: [SettingsRootSection]
    private let versionFooter: String

    init(sections: [SettingsRootSection], versionFooter: String) {
        self.sections = sections
        self.versionFooter = versionFooter
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

            Text(versionFooter)
                .textStyle(appTheme.typography.bodySmall)
                .foregroundColor(appTheme.colors.onSurfaceVariant)
                .frame(maxWidth: .infinity, alignment: .center)
                .padding(.vertical, appTheme.spacing.medium)
        }
    }
}

#if DEBUG
    #Preview {
        SettingsRootContentView(
            sections: SettingsPreviewSamples.rootSections(authenticated: true),
            versionFooter: "Version 1.0.0"
        )
        .padding()
        .appPreview()
    }
#endif
