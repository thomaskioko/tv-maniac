import SwiftUI

public struct FilterChip: View {
    @Theme private var theme

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
                        ? theme.colors.accent
                        : Color.clear
                )
                .overlay(
                    RoundedRectangle(cornerRadius: theme.shapes.small)
                        .strokeBorder(
                            isSelected
                                ? Color.clear
                                : theme.colors.onSurface.opacity(0.2),
                            lineWidth: 1
                        )
                )
                .foregroundColor(
                    isSelected
                        ? theme.colors.onAccent
                        : theme.colors.onSurface
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
