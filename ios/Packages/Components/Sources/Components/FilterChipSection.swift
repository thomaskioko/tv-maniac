import DesignSystem
import SwiftUI

public struct FilterChipSection<Item: Hashable>: View {
    @Environment(\.appTheme) private var theme

    private let title: String
    private let items: [Item]
    private let selectedItems: Set<Item>
    private let labelProvider: (Item) -> String
    private let onItemToggle: (Item) -> Void
    private let collapsedItemCount: Int
    private let showLessLabel: String
    private let showMoreLabel: String

    @State private var isExpanded = false

    public init(
        title: String,
        items: [Item],
        selectedItems: Set<Item>,
        labelProvider: @escaping (Item) -> String,
        onItemToggle: @escaping (Item) -> Void,
        collapsedItemCount: Int = 5,
        showLessLabel: String = "Show less",
        showMoreLabel: String = "Show more"
    ) {
        self.title = title
        self.items = items
        self.selectedItems = selectedItems
        self.labelProvider = labelProvider
        self.onItemToggle = onItemToggle
        self.collapsedItemCount = collapsedItemCount
        self.showLessLabel = showLessLabel
        self.showMoreLabel = showMoreLabel
    }

    private var visibleItems: [Item] {
        isExpanded ? items : Array(items.prefix(collapsedItemCount))
    }

    private var hasMoreItems: Bool {
        items.count > collapsedItemCount
    }

    public var body: some View {
        VStack(alignment: .leading, spacing: theme.spacing.small) {
            sectionHeader

            FlowLayout(spacing: theme.spacing.xSmall, items: visibleItems) { item in
                FilterChip(
                    label: labelProvider(item),
                    isSelected: selectedItems.contains(item),
                    onTap: { onItemToggle(item) }
                )
            }

            if hasMoreItems {
                showMoreToggle
            }
        }
    }

    private var sectionHeader: some View {
        HStack {
            dividerLine
            Text(title)
                .textStyle(theme.typography.labelMedium)
                .foregroundStyle(.appOnSurfaceVariant)
                .padding(.horizontal, theme.spacing.medium)
            dividerLine
        }
    }

    private var dividerLine: some View {
        Rectangle()
            .fill(.appOutline.opacity(0.3))
            .frame(height: 1)
    }

    private var showMoreToggle: some View {
        Button {
            withAnimation(.easeInOut(duration: 0.2)) {
                isExpanded.toggle()
            }
        } label: {
            HStack(spacing: theme.spacing.xxSmall) {
                Text(isExpanded ? showLessLabel : showMoreLabel)
                    .textStyle(theme.typography.bodyMedium)
                Image(systemName: isExpanded ? "chevron.up" : "chevron.down")
                    .textStyle(theme.typography.labelSmall)
            }
            .foregroundStyle(.appOnSurfaceVariant)
            .padding(.vertical, theme.spacing.xxSmall)
        }
        .buttonStyle(.plain)
        .padding(.top, theme.spacing.xSmall)
    }
}

#Preview {
    FilterChipSection(
        title: "GENRES",
        items: ["Action", "Comedy", "Drama", "Horror", "Romance", "Sci-Fi", "Thriller"],
        selectedItems: ["Drama", "Comedy"],
        labelProvider: { $0 },
        onItemToggle: { _ in }
    )
    .padding()
}
