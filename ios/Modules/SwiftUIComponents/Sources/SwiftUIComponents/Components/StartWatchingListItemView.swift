import DesignSystem
import SwiftUI

public struct StartWatchingListItemView: View {
    @Environment(\.appTheme) private var theme

    private let item: SwiftStartWatchingItem
    private let onItemClicked: (Int64) -> Void

    public init(
        item: SwiftStartWatchingItem,
        onItemClicked: @escaping (Int64) -> Void
    ) {
        self.item = item
        self.onItemClicked = onItemClicked
    }

    public var body: some View {
        Button(action: { onItemClicked(item.traktId) }) {
            HStack(alignment: .center, spacing: 0) {
                posterView
                details
            }
            .frame(height: StartWatchingListItemViewConstants.height)
            .frame(maxWidth: .infinity)
            .background(.appSurface)
            .cornerRadius(StartWatchingListItemViewConstants.cornerRadius)
        }
        .buttonStyle(PlainButtonStyle())
        .padding(.horizontal, theme.spacing.medium)
    }

    private var posterView: some View {
        PosterItemView(
            title: item.title,
            posterUrl: item.posterUrl,
            posterWidth: StartWatchingListItemViewConstants.imageWidth,
            posterHeight: StartWatchingListItemViewConstants.height,
            posterRadius: 0
        )
        .frame(width: StartWatchingListItemViewConstants.imageWidth, height: StartWatchingListItemViewConstants.height)
        .clipped()
    }

    private var details: some View {
        VStack(alignment: .leading, spacing: theme.spacing.xxSmall) {
            Text(item.title)
                .textStyle(theme.typography.titleMedium)
                .foregroundStyle(.appOnSurface)
                .lineLimit(2)

            if let year = item.year {
                Text(year)
                    .textStyle(theme.typography.bodySmall)
                    .foregroundStyle(.appOnSurfaceVariant)
            }
        }
        .frame(maxWidth: .infinity, alignment: .leading)
        .padding(.vertical, theme.spacing.small)
        .padding(.horizontal, theme.spacing.medium)
    }
}

private enum StartWatchingListItemViewConstants {
    static let height: CGFloat = 140
    static let imageWidth: CGFloat = 120
    static let cornerRadius: CGFloat = 2
}

#Preview {
    VStack(spacing: 8) {
        StartWatchingListItemView(
            item: SwiftStartWatchingItem(
                traktId: 1,
                title: "Breaking Bad",
                posterUrl: nil,
                year: "2008"
            ),
            onItemClicked: { _ in }
        )

        StartWatchingListItemView(
            item: SwiftStartWatchingItem(
                traktId: 2,
                title: "Severance: A Very Long Title That Should Wrap To Two Lines",
                posterUrl: nil,
                year: "2022"
            ),
            onItemClicked: { _ in }
        )
    }
    .appPreview()
}
