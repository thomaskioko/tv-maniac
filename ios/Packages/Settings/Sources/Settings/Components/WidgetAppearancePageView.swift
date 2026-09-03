import Components
import DesignSystem
import SwiftUI

struct WidgetAppearancePageView: View {
    @Environment(\.appTheme) private var appTheme
    private let item: SettingsWidgetAppearanceItem

    init(item: SettingsWidgetAppearanceItem) {
        self.item = item
    }

    var body: some View {
        VStack(alignment: .leading, spacing: appTheme.spacing.large) {
            description
            livePreview
            themeSelector
        }
        .frame(maxWidth: .infinity, alignment: .leading)
        .padding(.vertical, appTheme.spacing.medium)
        .containerRelativeFrame(.vertical, alignment: .top)
        .premiumOverlay(
            isLocked: item.isLocked,
            badgeText: item.lockedBadgeText,
            actionText: item.lockedActionText,
            onActionClick: item.onUpgradeClick,
            accessibilityLabel: item.lockedAccessibilityLabel
        )
    }

    private var description: some View {
        Text(item.description)
            .textStyle(appTheme.typography.bodyMedium)
            .foregroundColor(appTheme.colors.onSurfaceVariant)
    }

    private var livePreview: some View {
        VStack(alignment: .leading, spacing: appTheme.spacing.small) {
            sectionTitle(item.livePreviewLabel)

            VStack(alignment: .leading, spacing: appTheme.spacing.xSmall) {
                previewRow
                previewRow
            }
            .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: .topLeading)
            .padding(appTheme.spacing.small)
            .background(item.previewTheme.colors.surface)
            .clipShape(RoundedRectangle(cornerRadius: appTheme.shapes.medium))
            .padding(appTheme.spacing.small)
            .frame(height: DimensionConstants.previewHeight)
            .background(appTheme.colors.surfaceVariant)
            .clipShape(RoundedRectangle(cornerRadius: appTheme.shapes.medium))
        }
    }

    private var previewRow: some View {
        HStack(spacing: appTheme.spacing.small) {
            RoundedRectangle(cornerRadius: appTheme.spacing.xxSmall)
                .fill(item.previewTheme.colors.surfaceVariant)
                .frame(width: DimensionConstants.posterWidth, height: DimensionConstants.posterHeight)
                .overlay(
                    Image(systemName: "movieclapper")
                        .resizable()
                        .scaledToFit()
                        .frame(width: DimensionConstants.posterIconSize)
                        .foregroundColor(item.previewTheme.colors.onSurfaceVariant)
                )

            VStack(alignment: .leading, spacing: appTheme.spacing.xxxSmall) {
                bar(
                    width: DimensionConstants.titleBarWidth,
                    height: appTheme.spacing.small,
                    color: item.previewTheme.colors.onSurface
                )
                bar(
                    width: DimensionConstants.subtitleBarWidth,
                    height: appTheme.spacing.xSmall,
                    color: item.previewTheme.colors.onSurfaceVariant
                )
            }
        }
    }

    private func bar(width: CGFloat, height: CGFloat, color: Color) -> some View {
        RoundedRectangle(cornerRadius: appTheme.spacing.xxxSmall)
            .fill(color)
            .frame(width: width, height: height)
    }

    private var themeSelector: some View {
        VStack(alignment: .leading, spacing: appTheme.spacing.xSmall) {
            sectionTitle(item.themeLabel)
            FlowLayout(spacing: appTheme.spacing.xSmall, items: item.options) { option in
                SelectionChip(
                    label: option.label,
                    isSelected: option.id == item.selectedOptionId,
                    action: option.onSelect
                )
            }
        }
    }

    private func sectionTitle(_ text: String) -> some View {
        Text(text)
            .textStyle(appTheme.typography.titleSmall)
            .foregroundColor(appTheme.colors.onSurface)
    }
}

private enum DimensionConstants {
    static let previewHeight: CGFloat = 176
    static let posterWidth: CGFloat = 40
    static let posterHeight: CGFloat = 60
    static let posterIconSize: CGFloat = 20
    static let titleBarWidth: CGFloat = 112
    static let subtitleBarWidth: CGFloat = 72
}

#if DEBUG
    #Preview {
        WidgetAppearancePageView(item: SettingsPreviewSamples.widgetAppearanceItem)
            .padding()
            .appPreview()
    }

    #Preview("Locked") {
        WidgetAppearancePageView(item: SettingsPreviewSamples.lockedWidgetAppearanceItem)
            .padding()
            .appPreview()
    }
#endif
