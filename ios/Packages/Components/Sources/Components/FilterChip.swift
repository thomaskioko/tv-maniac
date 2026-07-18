import DesignSystem
import SwiftUI

public struct FilterChip: View {
    @Environment(\.appTheme) private var theme

    private let label: String
    private let isSelected: Bool
    private let onTap: () -> Void

    public init(
        label: String,
        isSelected: Bool,
        onTap: @escaping () -> Void
    ) {
        self.label = label
        self.isSelected = isSelected
        self.onTap = onTap
    }

    public var body: some View {
        Button(action: onTap) {
            Text(label)
                .textStyle(theme.typography.bodyMedium)
                .padding(.horizontal, theme.spacing.small)
                .padding(.vertical, theme.spacing.xSmall)
                .background(
                    isSelected
                        ? AnyShapeStyle(.appSecondary)
                        : AnyShapeStyle(.appSurfaceVariant.opacity(0.5))
                )
                .overlay(
                    RoundedRectangle(cornerRadius: theme.shapes.medium)
                        .strokeBorder(
                            isSelected
                                ? AnyShapeStyle(Color.clear)
                                : AnyShapeStyle(.appOnSurface.opacity(0.8)),
                            lineWidth: 1.5
                        )
                )
                .foregroundStyle(
                    isSelected
                        ? AnyShapeStyle(.appOnSecondary)
                        : AnyShapeStyle(.appOnSurface)
                )
                .clipShape(RoundedRectangle(cornerRadius: theme.shapes.medium))
        }
        .buttonStyle(.plain)
    }
}

#Preview {
    VStack(spacing: TvManiacSpacingScheme.default.medium) {
        FilterChip(
            label: "Last watched ↓",
            isSelected: true,
            onTap: {}
        )

        FilterChip(
            label: "Drama",
            isSelected: false,
            onTap: {}
        )
    }
    .padding()
}
