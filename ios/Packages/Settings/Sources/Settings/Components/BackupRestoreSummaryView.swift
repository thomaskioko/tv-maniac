import DesignSystem
import SwiftUI

public struct BackupRestoreSummaryView: View {
    @Environment(\.appTheme) private var appTheme
    private let content: SettingsBackupSummaryContent

    public init(content: SettingsBackupSummaryContent) {
        self.content = content
    }

    public var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: appTheme.spacing.medium) {
                Text(content.title)
                    .textStyle(appTheme.typography.titleLarge)
                    .foregroundColor(appTheme.colors.onSurface)
                    .frame(maxWidth: .infinity, alignment: .leading)

                countsCard

                if let showsSkipped = content.showsSkipped {
                    skippedCard(message: showsSkipped)
                }

                if let rewatchNotice = content.rewatchNotice {
                    Text(rewatchNotice)
                        .textStyle(appTheme.typography.bodySmall)
                        .foregroundColor(appTheme.colors.onSurfaceVariant)
                }
            }
            .padding(appTheme.spacing.medium)
        }
    }

    private var countsCard: some View {
        SettingsCard {
            countRow(icon: "tv", text: content.showsRestored)
            SettingsRowDivider()
            countRow(icon: "play.rectangle", text: content.episodesRestored)
        }
    }

    private func countRow(icon: String, text: String) -> some View {
        HStack(spacing: appTheme.spacing.medium) {
            SettingsIconChip(icon)
            Text(text)
                .textStyle(appTheme.typography.bodyLarge)
                .foregroundColor(appTheme.colors.onSurface)
            Spacer()
        }
        .padding(.horizontal, appTheme.spacing.medium)
        .padding(.vertical, appTheme.spacing.small)
    }

    private func skippedCard(message: String) -> some View {
        SettingsCard {
            VStack(alignment: .leading, spacing: appTheme.spacing.xSmall) {
                Text(message)
                    .textStyle(appTheme.typography.bodyMedium)
                    .foregroundColor(appTheme.colors.onSurface)
                ForEach(content.skippedShows, id: \.self) { show in
                    Text(show)
                        .textStyle(appTheme.typography.bodySmall)
                        .foregroundColor(appTheme.colors.onSurfaceVariant)
                }
            }
            .padding(appTheme.spacing.medium)
        }
    }
}

#if DEBUG
    #Preview {
        BackupRestoreSummaryView(content: SettingsPreviewSamples.restoreSummaryContent)
            .padding()
            .appPreview()
    }

    #Preview("With Skips") {
        BackupRestoreSummaryView(content: SettingsPreviewSamples.restoreSummaryContentWithSkips)
            .padding()
            .appPreview()
    }
#endif
