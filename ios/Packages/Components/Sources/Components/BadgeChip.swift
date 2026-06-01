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

#Preview {
    VStack(spacing: 12) {
        PremiereBadge(text: "Premiere")
        NewBadge(text: "New")
    }
}
