import Components
import DesignSystem
import SwiftUI

struct BackupPageView: View {
    @Environment(\.appTheme) private var appTheme
    private let content: SettingsBackupContent

    init(content: SettingsBackupContent) {
        self.content = content
    }

    var body: some View {
        VStack(alignment: .leading, spacing: appTheme.spacing.medium) {
            SettingsCard {
                exportRow
                SettingsRowDivider()
                importRow
            }

            if let summary = content.summary {
                BackupRestoreSummaryView(
                    content: summary,
                    dismissAccessibilityLabel: content.summaryDismissAccessibilityLabel,
                    onDismiss: content.onDismissSummary
                )
            }
        }
        .premiumOverlay(
            isLocked: content.isLocked,
            badgeText: content.lockedBadgeText,
            title: content.lockedTitle,
            message: content.lockedMessage,
            actionText: content.lockedActionText,
            onActionClick: content.onUpgradeClick,
            accessibilityLabel: content.lockedAccessibilityLabel
        )
    }

    private var exportRow: some View {
        Button(action: content.onExport) {
            HStack(spacing: appTheme.spacing.medium) {
                SettingsIconChip("square.and.arrow.up")

                VStack(alignment: .leading, spacing: appTheme.spacing.xxSmall) {
                    Text(content.exportTitle)
                        .textStyle(appTheme.typography.bodyLarge)
                        .foregroundColor(appTheme.colors.onSurface)
                    Text(content.exportDescription)
                        .textStyle(appTheme.typography.bodySmall)
                        .foregroundColor(appTheme.colors.onSurfaceVariant)
                }

                Spacer()

                if content.isExporting {
                    ProgressView()
                        .progressViewStyle(.circular)
                        .tint(appTheme.colors.onSurfaceVariant)
                } else {
                    Image(systemName: "chevron.right")
                        .foregroundColor(appTheme.colors.onSurfaceVariant)
                }
            }
            .padding(.horizontal, appTheme.spacing.medium)
            .padding(.vertical, appTheme.spacing.small)
            .contentShape(Rectangle())
        }
        .buttonStyle(.plain)
        .disabled(content.isExporting || content.isLocked)
        .accessibilityLabel(content.exportTitle)
        .accessibilityHint(content.exportDescription)
    }

    private var importRow: some View {
        Button(action: content.onImport) {
            HStack(spacing: appTheme.spacing.medium) {
                SettingsIconChip("square.and.arrow.down")

                VStack(alignment: .leading, spacing: appTheme.spacing.xxSmall) {
                    Text(content.importTitle)
                        .textStyle(appTheme.typography.bodyLarge)
                        .foregroundColor(appTheme.colors.onSurface)
                    Text(content.importDescription)
                        .textStyle(appTheme.typography.bodySmall)
                        .foregroundColor(appTheme.colors.onSurfaceVariant)
                }

                Spacer()

                if content.isImporting {
                    ProgressView()
                        .progressViewStyle(.circular)
                        .tint(appTheme.colors.onSurfaceVariant)
                } else {
                    Image(systemName: "chevron.right")
                        .foregroundColor(appTheme.colors.onSurfaceVariant)
                }
            }
            .padding(.horizontal, appTheme.spacing.medium)
            .padding(.vertical, appTheme.spacing.small)
            .contentShape(Rectangle())
        }
        .buttonStyle(.plain)
        .disabled(content.isImporting || content.isLocked)
        .accessibilityLabel(content.importTitle)
        .accessibilityHint(content.importDescription)
    }
}

#if DEBUG
    #Preview {
        BackupPageView(content: SettingsPreviewSamples.backupContent)
            .padding()
            .appPreview()
    }

    #Preview("Locked") {
        BackupPageView(content: SettingsPreviewSamples.lockedBackupContent)
            .padding()
            .appPreview()
    }

    #Preview("Exporting") {
        BackupPageView(content: SettingsPreviewSamples.exportingBackupContent)
            .padding()
            .appPreview()
    }

    #Preview("Importing") {
        BackupPageView(content: SettingsPreviewSamples.importingBackupContent)
            .padding()
            .appPreview()
    }

    #Preview("Restore Summary") {
        BackupPageView(content: SettingsPreviewSamples.backupContentWithSummary)
            .padding()
            .appPreview()
    }

    #Preview("Restore Summary With Skips") {
        BackupPageView(content: SettingsPreviewSamples.backupContentWithSummaryAndSkips)
            .padding()
            .appPreview()
    }
#endif
