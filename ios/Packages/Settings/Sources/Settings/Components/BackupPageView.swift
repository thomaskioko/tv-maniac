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
        SettingsCard {
            exportRow
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
#endif
