import Components
import DesignSystem
import SwiftUI

public struct RecentSearchesSectionView: View {
    @Environment(\.appTheme) private var theme

    private let title: String
    private let clearButtonText: String
    private let recentSearches: [String]
    private let onRecentSearchSelected: (String) -> Void
    private let onClearRecentSearches: () -> Void

    public init(
        title: String,
        clearButtonText: String,
        recentSearches: [String],
        onRecentSearchSelected: @escaping (String) -> Void,
        onClearRecentSearches: @escaping () -> Void
    ) {
        self.title = title
        self.clearButtonText = clearButtonText
        self.recentSearches = recentSearches
        self.onRecentSearchSelected = onRecentSearchSelected
        self.onClearRecentSearches = onClearRecentSearches
    }

    public var body: some View {
        VStack(alignment: .leading, spacing: theme.spacing.xSmall) {
            header

            ScrollView(.horizontal, showsIndicators: false) {
                HStack(spacing: theme.spacing.xSmall) {
                    ForEach(recentSearches, id: \.self) { query in
                        FilterChip(
                            label: query,
                            isSelected: false,
                            onTap: { onRecentSearchSelected(query) }
                        )
                    }
                }
                .padding(.horizontal, theme.spacing.medium)
            }
        }
        .padding(.vertical, theme.spacing.xSmall)
    }

    private var header: some View {
        HStack(alignment: .firstTextBaseline) {
            Text(title)
                .textStyle(theme.typography.titleMedium)
                .foregroundStyle(.appOnSurface)

            Spacer()

            Button(action: onClearRecentSearches) {
                Text(clearButtonText)
                    .textStyle(theme.typography.bodyMedium)
                    .foregroundStyle(.appAccent)
            }
        }
        .padding(.horizontal, theme.spacing.medium)
    }
}

#Preview {
    RecentSearchesSectionView(
        title: "Recent searches",
        clearButtonText: "Clear",
        recentSearches: ["Arcane", "The Penguin", "Kaos", "One Piece"],
        onRecentSearchSelected: { _ in },
        onClearRecentSearches: {}
    )
    .padding(.vertical)
    .background(.appBackground)
}
