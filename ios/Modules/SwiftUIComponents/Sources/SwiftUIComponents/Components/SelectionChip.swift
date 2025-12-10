import SwiftUI

public struct SelectionChip: View {
    @Theme private var theme
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
                .foregroundColor(isSelected ? theme.colors.onSecondary : theme.colors.onSurface)
                .padding(.horizontal, theme.spacing.medium)
                .padding(.vertical, theme.spacing.small)
                .background(isSelected ? theme.colors.secondary : Color.clear)
                .overlay(
                    RoundedRectangle(cornerRadius: 20)
                        .stroke(isSelected ? Color.clear : theme.colors.outline, lineWidth: 1)
                )
                .cornerRadius(20)
        }
        .buttonStyle(.plain)
    }
}

public struct SelectionChipGroup: View {
    @Theme private var theme
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
    VStack(spacing: 16) {
        SelectionChip(label: "High", isSelected: true, action: {})
        SelectionChip(label: "Medium", isSelected: false, action: {})
        SelectionChipGroup(
            options: ["High", "Medium", "Low"],
            selectedIndex: 0,
            onSelect: { _ in }
        )
    }
    .padding()
    .background(Color(.systemBackground))
}
