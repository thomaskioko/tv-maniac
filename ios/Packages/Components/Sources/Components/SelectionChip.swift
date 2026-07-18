import DesignSystem
import SwiftUI

public struct SelectionChip: View {
    @Environment(\.appTheme) private var theme
    private let label: String
    private let isSelected: Bool
    private let action: () -> Void

    public init(
        label: String,
        isSelected: Bool,
        action: @escaping () -> Void
    ) {
        self.label = label
        self.isSelected = isSelected
        self.action = action
    }

    public var body: some View {
        Button(action: action) {
            Text(label)
                .textStyle(theme.typography.bodyMedium)
                .foregroundStyle(isSelected ? AnyShapeStyle(.appOnSecondary) : AnyShapeStyle(.appOnSurface))
                .padding(.horizontal, theme.spacing.medium)
                .padding(.vertical, theme.spacing.small)
                .background(isSelected ? AnyShapeStyle(.appSecondary) : AnyShapeStyle(.appSurfaceVariant.opacity(0.5)))
                .overlay(
                    RoundedRectangle(cornerRadius: theme.shapes.medium)
                        .stroke(isSelected ? AnyShapeStyle(Color.clear) : AnyShapeStyle(.appOnSurface.opacity(0.8)), lineWidth: 1)
                )
                .cornerRadius(theme.shapes.medium)
        }
        .buttonStyle(.plain)
    }
}

public struct SelectionChipGroup: View {
    @Environment(\.appTheme) private var theme
    let options: [String]
    let selectedIndex: Int
    let onSelect: (Int) -> Void

    public init(
        options: [String],
        selectedIndex: Int,
        onSelect: @escaping (Int) -> Void
    ) {
        self.options = options
        self.selectedIndex = selectedIndex
        self.onSelect = onSelect
    }

    public var body: some View {
        HStack(spacing: theme.spacing.xSmall) {
            ForEach(options.indices, id: \.self) { index in
                SelectionChip(
                    label: options[index],
                    isSelected: index == selectedIndex,
                    action: { onSelect(index) }
                )
            }
        }
    }
}

#Preview {
    VStack(spacing: TvManiacSpacingScheme.default.medium) {
        SelectionChip(label: "High", isSelected: true, action: {})
        SelectionChip(label: "Medium", isSelected: false, action: {})
        SelectionChipGroup(
            options: ["High", "Medium", "Low"],
            selectedIndex: 0,
            onSelect: { _ in }
        )
    }
    .padding()
    .background(.appBackground)
}
