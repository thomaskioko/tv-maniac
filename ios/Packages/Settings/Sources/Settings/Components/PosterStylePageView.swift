import Components
import DesignSystem
import SwiftUI

struct PosterStylePageView: View {
    @Environment(\.appTheme) private var appTheme
    private let item: SettingsPosterStyleItem

    init(item: SettingsPosterStyleItem) {
        self.item = item
    }

    var body: some View {
        SettingsCard {
            VStack(alignment: .leading, spacing: appTheme.spacing.large) {
                header
                description
                livePreview
                selector(
                    label: item.postersLabel,
                    options: item.postersOptions,
                    selectedId: item.selectedPostersId
                )
                selector(
                    label: item.landscapeLabel,
                    options: item.landscapeOptions,
                    selectedId: item.selectedLandscapeId
                )
                selector(
                    label: item.cornerLabel,
                    options: item.cornerOptions,
                    selectedId: item.selectedCornerId
                )
            }
            .padding(appTheme.spacing.medium)
        }
        .premiumOverlay(
            isLocked: item.isLocked,
            badgeText: item.lockedBadgeText,
            actionText: item.lockedActionText,
            onActionClick: item.onUpgradeClick,
            accessibilityLabel: item.lockedAccessibilityLabel
        )
    }

    private var header: some View {
        HStack(alignment: .center) {
            Text(item.title)
                .textStyle(appTheme.typography.titleMedium)
                .foregroundColor(appTheme.colors.onSurface)

            Spacer()

            Button(action: item.onReset) {
                Text(item.resetLabel)
                    .textStyle(appTheme.typography.labelLarge)
                    .foregroundColor(appTheme.colors.secondary)
            }
            .buttonStyle(.plain)
            .disabled(item.isLocked)
        }
    }

    private var description: some View {
        Text(item.description)
            .textStyle(appTheme.typography.bodyMedium)
            .foregroundColor(appTheme.colors.onSurfaceVariant)
    }

    private var livePreview: some View {
        VStack(alignment: .leading, spacing: appTheme.spacing.small) {
            SettingsSectionLabel(item.livePreviewLabel)

            HStack(alignment: .bottom, spacing: appTheme.spacing.large) {
                previewCard(
                    label: item.postersLabel,
                    baseWidth: DimensionConstants.posterPreviewBaseWidth,
                    scale: item.posterScale,
                    aspectRatio: ImageDimens.posterAspect
                )
                previewCard(
                    label: item.landscapeLabel,
                    baseWidth: DimensionConstants.landscapePreviewBaseWidth,
                    scale: item.landscapeScale,
                    aspectRatio: ImageDimens.backdropAspect
                )
                Spacer()
            }
            .animation(.spring(response: 0.3, dampingFraction: 0.8), value: item.posterScale)
            .animation(.spring(response: 0.3, dampingFraction: 0.8), value: item.landscapeScale)
            .animation(.spring(response: 0.3, dampingFraction: 0.8), value: item.cornerRadius)
        }
    }

    private func previewCard(label: String, baseWidth: CGFloat, scale: CGFloat, aspectRatio: CGFloat) -> some View {
        let width = baseWidth * scale
        return VStack(alignment: .center, spacing: appTheme.spacing.xSmall) {
            PosterItemView(
                title: nil,
                posterUrl: nil,
                posterWidth: width,
                aspectRatio: aspectRatio,
                posterRadius: item.cornerRadius
            )
            .frame(width: width, height: width / aspectRatio)

            Text(label)
                .textStyle(appTheme.typography.labelMedium)
                .foregroundColor(appTheme.colors.onSurfaceVariant)
        }
    }

    private func selector(label: String, options: [SettingsPosterStyleOption], selectedId: String) -> some View {
        VStack(alignment: .leading, spacing: appTheme.spacing.xSmall) {
            SettingsSectionLabel(label)
            HStack(spacing: appTheme.spacing.small) {
                ForEach(options) { option in
                    SelectionChip(
                        label: option.label,
                        isSelected: option.id == selectedId,
                        action: option.onSelect
                    )
                }
            }
        }
    }
}

private enum DimensionConstants {
    static let posterPreviewBaseWidth: CGFloat = 96
    static let landscapePreviewBaseWidth: CGFloat = 152
}

#if DEBUG
    #Preview {
        PosterStylePageView(item: SettingsPreviewSamples.posterStyleItem)
            .padding()
            .appPreview()
    }

    #Preview("Locked") {
        PosterStylePageView(item: SettingsPreviewSamples.lockedPosterStyleItem)
            .padding()
            .appPreview()
    }
#endif
