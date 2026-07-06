import Components
import DesignSystem
import SwiftUI

struct SettingsCard<Content: View>: View {
    @Environment(\.appTheme) private var appTheme
    private let content: Content

    init(@ViewBuilder content: () -> Content) {
        self.content = content()
    }

    var body: some View {
        VStack(alignment: .leading, spacing: 0) {
            content
        }
        .frame(maxWidth: .infinity, alignment: .leading)
        .background(appTheme.colors.surface)
        .clipShape(RoundedRectangle(cornerRadius: appTheme.shapes.large))
        .overlay(
            RoundedRectangle(cornerRadius: appTheme.shapes.large)
                .stroke(appTheme.colors.outline.opacity(0.2), lineWidth: 0.5)
        )
    }
}

struct SettingsSectionLabel: View {
    @Environment(\.appTheme) private var appTheme
    private let title: String

    init(_ title: String) {
        self.title = title
    }

    var body: some View {
        Text(title)
            .textStyle(appTheme.typography.labelMedium)
            .foregroundColor(appTheme.colors.onSurfaceVariant)
            .padding(.leading, appTheme.spacing.small)
    }
}

struct SettingsRowDivider: View {
    @Environment(\.appTheme) private var appTheme

    var body: some View {
        Rectangle()
            .fill(appTheme.colors.outline.opacity(0.2))
            .frame(height: 0.5)
            .padding(.leading, 64)
    }
}

struct SettingsIconChip: View {
    @Environment(\.appTheme) private var appTheme
    private let systemName: String

    init(_ systemName: String) {
        self.systemName = systemName
    }

    var body: some View {
        RoundedRectangle(cornerRadius: appTheme.shapes.medium)
            .fill(appTheme.colors.secondary.opacity(0.12))
            .frame(width: 36, height: 36)
            .overlay(
                Image(systemName: systemName)
                    .foregroundColor(appTheme.colors.secondary)
            )
    }
}

struct SettingsNavigationRow: View {
    @Environment(\.appTheme) private var appTheme
    private let item: SettingsNavigationItem

    init(_ item: SettingsNavigationItem) {
        self.item = item
    }

    var body: some View {
        Button(action: item.onTap) {
            HStack(spacing: appTheme.spacing.medium) {
                SettingsIconChip(item.icon)

                VStack(alignment: .leading, spacing: appTheme.spacing.xxSmall) {
                    Text(item.title)
                        .textStyle(appTheme.typography.bodyLarge)
                        .foregroundColor(appTheme.colors.onSurface)
                    if let subtitle = item.subtitle {
                        Text(subtitle)
                            .textStyle(appTheme.typography.bodySmall)
                            .foregroundColor(appTheme.colors.onSurfaceVariant)
                    }
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
    }
}

struct SettingsToggleRow: View {
    @Environment(\.appTheme) private var appTheme
    private let item: SettingsToggleItem

    init(_ item: SettingsToggleItem) {
        self.item = item
    }

    var body: some View {
        HStack(spacing: appTheme.spacing.medium) {
            SettingsIconChip(item.icon)

            VStack(alignment: .leading, spacing: appTheme.spacing.xxSmall) {
                Text(item.title)
                    .textStyle(appTheme.typography.bodyLarge)
                    .foregroundColor(appTheme.colors.onSurface)
                    .lineLimit(1)
                if item.isLocked {
                    LockBadge(
                        text: item.lockedBadgeText,
                        accessibilityLabel: item.lockedAccessibilityLabel
                    )
                }
                Text(item.subtitle)
                    .textStyle(appTheme.typography.bodySmall)
                    .foregroundColor(appTheme.colors.onSurfaceVariant)
                if let secondarySubtitle = item.secondarySubtitle {
                    Text(secondarySubtitle)
                        .textStyle(appTheme.typography.bodySmall)
                        .foregroundColor(appTheme.colors.onSurfaceVariant)
                }
            }

            Spacer()

            Toggle("", isOn: Binding(
                get: { item.isOn },
                set: { item.onToggle($0) }
            ))
            .labelsHidden()
            .tint(appTheme.colors.secondary)
            .disabled(item.isLocked)
        }
        .padding(.horizontal, appTheme.spacing.medium)
        .padding(.vertical, appTheme.spacing.small)
    }
}

struct SettingsLinkRow: View {
    @Environment(\.appTheme) private var appTheme
    private let item: SettingsLinkItem

    init(_ item: SettingsLinkItem) {
        self.item = item
    }

    var body: some View {
        Button(action: item.onOpen) {
            HStack(alignment: .top, spacing: appTheme.spacing.medium) {
                if let asset = item.leadingAsset {
                    Image(asset, bundle: .module)
                        .resizable()
                        .scaledToFit()
                        .frame(width: 36, height: 36)
                } else if let leading = item.leadingSystemImage {
                    Image(systemName: leading)
                        .foregroundColor(appTheme.colors.secondary)
                        .frame(width: appTheme.spacing.large, height: appTheme.spacing.large)
                }

                VStack(alignment: .leading, spacing: appTheme.spacing.xxSmall) {
                    Text(item.title)
                        .textStyle(appTheme.typography.titleMedium)
                        .foregroundColor(appTheme.colors.onSurface)
                    Text(item.body)
                        .textStyle(appTheme.typography.bodyMedium)
                        .foregroundColor(appTheme.colors.onSurfaceVariant)
                    Text(item.link)
                        .textStyle(appTheme.typography.bodySmall)
                        .foregroundColor(appTheme.colors.secondary)
                        .lineLimit(1)
                }

                Spacer()

                Image(systemName: "arrow.up.right.square")
                    .foregroundColor(appTheme.colors.onSurfaceVariant)
            }
            .padding(.horizontal, appTheme.spacing.medium)
            .padding(.vertical, appTheme.spacing.small)
            .contentShape(Rectangle())
        }
        .buttonStyle(.plain)
    }
}
