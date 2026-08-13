import Components
import DesignSystem
import SwiftUI

public struct LibraryCompactRowView: View {
    @Environment(\.appTheme) private var theme
    @Environment(\.widthSizeClass) private var widthSizeClass

    private let item: SwiftLibraryItem
    private let onItemClicked: () -> Void

    public init(
        item: SwiftLibraryItem,
        onItemClicked: @escaping () -> Void
    ) {
        self.item = item
        self.onItemClicked = onItemClicked
    }

    public var body: some View {
        Button(action: onItemClicked) {
            HStack(spacing: 0) {
                // The artwork sits flush against the card edge and sets the row height.
                PosterItemView(
                    title: item.title,
                    posterUrl: item.posterUrl,
                    posterWidth: ImageDimens.compactThumbnailWidth(widthSizeClass),
                    aspectRatio: ImageType.poster.aspect,
                    posterRadius: 0
                )

                VStack(alignment: .leading, spacing: theme.spacing.xxSmall) {
                    Text(item.title)
                        .textStyle(theme.typography.titleSmall)
                        .foregroundStyle(.appOnSurface)
                        .lineLimit(1)

                    if !item.metadataComponents.isEmpty {
                        metadataText(item.metadataComponents, accent: theme.colors.accent)
                            .textStyle(theme.typography.bodySmall)
                            .foregroundStyle(.appOnSurfaceVariant)
                            .lineLimit(1)
                    }
                }
                .padding(theme.spacing.small)

                Spacer(minLength: 0)
            }
            .background(.appSurface)
            .clipShape(RoundedRectangle(cornerRadius: theme.shapes.medium, style: .continuous))
            .appShadow(theme.shadows.medium)
        }
        .buttonStyle(.plain)
    }
}

#Preview {
    VStack(spacing: TvManiacSpacingScheme.default.xxSmall) {
        LibraryCompactRowView(
            item: SwiftLibraryItem(
                showId: 1,
                title: "Breaking Bad",
                posterUrl: nil,
                year: "2008",
                status: "Ended",
                seasonCount: 5,
                episodeCount: 62,
                rating: 9.5,
                genres: ["Drama", "Crime", "Thriller"],
                watchProviders: []
            ),
            onItemClicked: {}
        )

        LibraryCompactRowView(
            item: SwiftLibraryItem(
                showId: 2,
                title: "Game of Thrones: A Very Long Title That Should Truncate",
                posterUrl: nil,
                year: "2011",
                status: "Ended",
                seasonCount: 8,
                episodeCount: 73,
                rating: 9.2,
                genres: ["Fantasy", "Drama"],
                watchProviders: []
            ),
            onItemClicked: {}
        )
    }
    .padding()
}
