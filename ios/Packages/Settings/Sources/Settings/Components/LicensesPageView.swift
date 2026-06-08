import DesignSystem
import SwiftUI

struct LicensesPageView: View {
    @Environment(\.appTheme) private var appTheme
    private let sections: [SettingsLicenseSection]

    init(sections: [SettingsLicenseSection]) {
        self.sections = sections
    }

    var body: some View {
        VStack(alignment: .leading, spacing: appTheme.spacing.large) {
            ForEach(sections) { section in
                VStack(alignment: .leading, spacing: appTheme.spacing.small) {
                    SettingsSectionLabel(section.label)
                    SettingsCard {
                        ForEach(Array(section.items.enumerated()), id: \.element.id) { index, item in
                            SettingsLinkRow(item)
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
        LicensesPageView(sections: SettingsPreviewSamples.licenseSections)
            .padding()
            .appPreview()
    }
#endif
