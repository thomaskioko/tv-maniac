import Components
import DesignSystem
import Models
import SwiftUI

public struct LibraryListItemView: View {
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
            HStack(alignment: .top, spacing: theme.spacing.medium) {
                PosterItemView(
                    title: item.title,
                    posterUrl: item.posterUrl,
                    posterWidth: ImageDimens.posterWidthFixed(widthSizeClass),
                    aspectRatio: ImageType.poster.aspect
                )

                VStack(alignment: .leading, spacing: theme.spacing.xxSmall) {
                    Text(item.title)
                        .textStyle(theme.typography.titleMedium)
                        .foregroundStyle(.appOnSurface)
                        .lineLimit(2)

                    if let rating = item.formattedRating {
                        HStack(spacing: theme.spacing.xxSmall) {
                            Image(systemName: "star.fill")
                                .textStyle(theme.typography.bodyMedium)
                                .foregroundStyle(.appAccent)
                            Text(rating)
                                .textStyle(theme.typography.bodyMedium)
                                .foregroundStyle(.appOnSurface)
                        }
                    }

                    if !item.metadataComponents.isEmpty {
                        metadataText(item.metadataComponents, accent: theme.colors.accent)
                            .textStyle(theme.typography.bodySmall)
                            .foregroundStyle(.appOnSurfaceVariant)
                            .lineLimit(3)
                    }

                    if !item.watchProviders.isEmpty {
                        HStack(spacing: theme.spacing.xxSmall) {
                            ForEach(Array(item.watchProviders.prefix(6))) { provider in
                                LazyResizableImage(
                                    url: provider.logoUrl,
                                    size: CGSize(width: 32, height: 32)
                                )
                                .frame(width: 32, height: 32)
                                .clipShape(RoundedRectangle(cornerRadius: theme.shapes.small))
                            }
                        }
                    }
                }
                .frame(maxWidth: .infinity, alignment: .leading)
                .padding(.vertical, theme.spacing.small)
                .padding(.trailing, theme.spacing.small)
            }
            .background(.appSurface)
            .clipShape(RoundedRectangle(cornerRadius: theme.shapes.small))
            .appShadow(theme.shadows.medium)
        }
        .buttonStyle(.plain)
    }
}

#Preview {
    VStack(spacing: TvManiacSpacingScheme.default.medium) {
        LibraryListItemView(
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
                watchProviders: [
                    SwiftProviders(providerId: 1, logoUrl: nil),
                    SwiftProviders(providerId: 2, logoUrl: nil),
                ]
            ),
            onItemClicked: {}
        )

        LibraryListItemView(
            item: SwiftLibraryItem(
                showId: 2,
                title: "Game of Thrones: A Very Long Title That Should Wrap",
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
