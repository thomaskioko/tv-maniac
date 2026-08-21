import Components
import DesignSystem
import SwiftUI
import TvManiac

private let disabledOpacity: Double = 0.38

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
                SettingsRowDivider()
                locationRow
            }

            if let summary = content.summary {
                BackupRestoreSummaryView(
                    content: summary,
                    dismissAccessibilityLabel: content.summaryDismissAccessibilityLabel,
                    onDismiss: content.onDismissSummary
                )
            }

            autoBackupSection
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

    private var locationRow: some View {
        Button(action: content.autoBackup.onChooseLocation) {
            HStack(spacing: appTheme.spacing.medium) {
                SettingsIconChip("folder")

                VStack(alignment: .leading, spacing: appTheme.spacing.xxSmall) {
                    Text(content.autoBackup.locationTitle)
                        .textStyle(appTheme.typography.bodyLarge)
                        .foregroundColor(appTheme.colors.onSurface)
                    Text(content.autoBackup.locationLabel)
                        .textStyle(appTheme.typography.bodySmall)
                        .foregroundColor(appTheme.colors.onSurfaceVariant)
                }

                Spacer()

                Image(systemName: "chevron.right")
                    .foregroundColor(appTheme.colors.onSurfaceVariant)
            }
            .padding(.horizontal, appTheme.spacing.medium)
            .padding(.vertical, appTheme.spacing.small)
            .contentShape(Rectangle())
        }
        .buttonStyle(.plain)
        .disabled(content.isLocked)
        .testTag(SettingsTestTags.shared.AUTO_BACKUP_LOCATION_ROW_TEST_TAG)
        .accessibilityLabel(content.autoBackup.locationTitle)
        .accessibilityHint(content.autoBackup.locationLabel)
    }

    private var autoBackupSection: some View {
        VStack(alignment: .leading, spacing: appTheme.spacing.medium) {
            SettingsCard {
                automaticBackupToggle

                if content.autoBackup.isOn {
                    SettingsRowDivider()
                    backupNowRow
                }
            }

            if content.autoBackup.isOn {
                SettingsCard {
                    VStack(alignment: .leading, spacing: appTheme.spacing.small) {
                        Text(content.autoBackup.scheduleTitle)
                            .textStyle(appTheme.typography.titleSmall)
                            .foregroundColor(appTheme.colors.onSurface)

                        FlowLayout(
                            spacing: appTheme.spacing.small,
                            items: content.autoBackup.scheduleOptions
                        ) { option in
                            SelectionChip(
                                label: option.label,
                                isSelected: option.isSelected,
                                action: option.onSelect
                            )
                            .testTag(SettingsTestTags.shared.autoBackupScheduleChip(name: option.id))
                        }
                    }
                    .padding(appTheme.spacing.medium)
                }

                VStack(alignment: .leading, spacing: appTheme.spacing.xxSmall) {
                    Text(content.autoBackup.lastRunLabel)
                        .textStyle(appTheme.typography.bodySmall)
                        .foregroundColor(appTheme.colors.onSurfaceVariant)

                    if let failureWarning = content.autoBackup.failureWarning {
                        Text(failureWarning)
                            .textStyle(appTheme.typography.bodySmall)
                            .foregroundColor(appTheme.colors.error)
                    }
                }
                .padding(.horizontal, appTheme.spacing.medium)
            }
        }
    }

    private var automaticBackupToggle: some View {
        SettingsToggleRow(
            SettingsToggleItem(
                id: "auto_backup",
                icon: "clock",
                title: content.autoBackup.title,
                subtitle: content.autoBackup.description,
                isOn: content.autoBackup.isOn,
                onToggle: content.autoBackup.onToggle
            )
        )
        .testTag(SettingsTestTags.shared.AUTO_BACKUP_TOGGLE_TEST_TAG)
    }

    private var backupNowRow: some View {
        Button(action: content.autoBackup.onBackupNow) {
            HStack(spacing: appTheme.spacing.medium) {
                SettingsIconChip("bolt")

                VStack(alignment: .leading, spacing: appTheme.spacing.xxSmall) {
                    Text(content.autoBackup.backupNowTitle)
                        .textStyle(appTheme.typography.bodyLarge)
                        .foregroundColor(appTheme.colors.onSurface)
                    Text(content.autoBackup.backupNowDescription)
                        .textStyle(appTheme.typography.bodySmall)
                        .foregroundColor(appTheme.colors.onSurfaceVariant)
                }

                Spacer()

                if content.autoBackup.isBackingUp {
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
        .disabled(content.autoBackup.isBackingUp || !content.autoBackup.hasLocation || content.isLocked)
        .opacity(content.autoBackup.hasLocation ? 1 : disabledOpacity)
        .testTag(SettingsTestTags.shared.AUTO_BACKUP_NOW_ROW_TEST_TAG)
        .accessibilityLabel(content.autoBackup.backupNowTitle)
        .accessibilityHint(content.autoBackup.backupNowDescription)
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

    #Preview("Automatic Backup On") {
        BackupPageView(content: SettingsPreviewSamples.autoBackupOnBackupContent)
            .padding()
            .appPreview()
    }

    #Preview("Automatic Backup Never Run") {
        BackupPageView(content: SettingsPreviewSamples.autoBackupNeverRunBackupContent)
            .padding()
            .appPreview()
    }

    #Preview("Automatic Backup Failed") {
        BackupPageView(content: SettingsPreviewSamples.autoBackupFailedBackupContent)
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
