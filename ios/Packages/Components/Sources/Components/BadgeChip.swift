import DesignSystem
import SwiftUI

public struct PremiereBadge: View {
    @Environment(\.appTheme) private var theme

    private let text: String

    public init(text: String) {
        self.text = text
    }

    public var body: some View {
        Text(text)
            .textStyle(theme.typography.labelSmall)
            .foregroundStyle(theme.colors.background)
            .padding(.horizontal, theme.spacing.xSmall)
            .padding(.vertical, theme.spacing.xxSmall)
            .background(
                RoundedRectangle(cornerRadius: theme.shapes.small)
                    .fill(theme.colors.onSurface)
            )
    }
}

public struct NewBadge: View {
    @Environment(\.appTheme) private var theme

    private let text: String

    public init(text: String) {
        self.text = text
    }

    public var body: some View {
        Text(text)
            .textStyle(theme.typography.labelSmall)
            .foregroundStyle(theme.colors.onSecondary)
            .padding(.horizontal, theme.spacing.xSmall)
            .padding(.vertical, theme.spacing.xxSmall)
            .background(
                RoundedRectangle(cornerRadius: theme.shapes.small)
                    .fill(theme.colors.secondary)
            )
    }
}

public struct LockBadge: View {
    @Environment(\.appTheme) private var theme

    private let text: String
    private let accessibilityLabel: String?

    public init(text: String, accessibilityLabel: String? = nil) {
        self.text = text
        self.accessibilityLabel = accessibilityLabel
    }

    public var body: some View {
        HStack(spacing: theme.spacing.xxSmall) {
            Image(systemName: "lock.fill")
                .textStyle(theme.typography.labelSmall)
            Text(text)
                .textStyle(theme.typography.labelSmall)
                .lineLimit(1)
        }
        .fixedSize()
        .foregroundStyle(theme.colors.background)
        .padding(.horizontal, theme.spacing.xSmall)
        .padding(.vertical, theme.spacing.xxSmall)
        .background(
            RoundedRectangle(cornerRadius: theme.shapes.small)
                .fill(theme.colors.onSurface)
        )
        .accessibilityElement(children: .ignore)
        .accessibilityLabel(accessibilityLabel ?? text)
    }
}

#Preview {
    VStack(spacing: TvManiacSpacingScheme.default.small) {
        PremiereBadge(text: "Premiere")
        NewBadge(text: "New")
        LockBadge(text: "Premium")
    }
}
