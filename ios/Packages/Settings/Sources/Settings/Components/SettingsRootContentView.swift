import DesignSystem
import SwiftUI
import TvManiacKit

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
                                .testTag(rootRowTestTag(for: item.id))
                            if index != section.items.count - 1 {
                                SettingsRowDivider()
                            }
                        }
                    }
                }
            }
        }
    }

    private func rootRowTestTag(for id: String) -> String? {
        let tags = SettingsTestTags.shared
        switch SettingsPageRoute(rawValue: id) {
        case .appearance: return tags.GENERAL_APPEARANCE_ROW_TEST_TAG
        case .layout: return tags.GENERAL_LAYOUT_ROW_TEST_TAG
        case .behavior: return tags.GENERAL_BEHAVIOR_ROW_TEST_TAG
        case .notifications: return tags.GENERAL_NOTIFICATIONS_ROW_TEST_TAG
        case .privacy: return tags.GENERAL_PRIVACY_ROW_TEST_TAG
        case .info: return tags.ABOUT_INFO_ROW_TEST_TAG
        case .licenses: return tags.ABOUT_LICENSES_ROW_TEST_TAG
        case .account: return tags.ACCOUNT_TRAKT_ROW_TEST_TAG
        default: return nil
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
