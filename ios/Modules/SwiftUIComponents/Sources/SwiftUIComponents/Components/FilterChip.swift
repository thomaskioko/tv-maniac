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
                        ? AnyShapeStyle(.appAccent)
                        : AnyShapeStyle(Color.clear)
                )
                .overlay(
                    RoundedRectangle(cornerRadius: theme.shapes.small)
                        .strokeBorder(
                            isSelected
                                ? AnyShapeStyle(Color.clear)
                                : AnyShapeStyle(.appOnSurface.opacity(0.8)),
                            lineWidth: 1.5
                        )
                )
                .foregroundStyle(
                    isSelected
                        ? AnyShapeStyle(.appOnAccent)
                        : AnyShapeStyle(.appOnSurface)
                )
                .clipShape(RoundedRectangle(cornerRadius: theme.shapes.small))
        }
        .buttonStyle(.plain)
    }
}

#Preview {
    VStack(spacing: 16) {
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
