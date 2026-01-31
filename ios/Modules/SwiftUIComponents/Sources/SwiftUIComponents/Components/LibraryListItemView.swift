import SwiftUI

public struct LibraryListItemView: View {
    @Theme private var theme

    private let item: SwiftLibraryItem
    private let onItemClicked: () -> Void

    public init(
        item: SwiftLibraryItem,
        onItemClicked: @escaping () -> Void
    ) {
        self.item = item
        self.onItemClicked = onItemClicked
    }

    private var metadataText: String? {
        var components: [String] = []
        if let year = item.year {
            components.append(year)
        }
        if let status = item.status {
            components.append(status)
        }
        let seasonCount = Int(item.seasonCount)
        if seasonCount > 0 {
            components.append(seasonCount == 1 ? "\(seasonCount) Season" : "\(seasonCount) Seasons")
        }
        let episodeCount = Int(item.episodeCount)
        if episodeCount > 0 {
            components.append(episodeCount == 1 ? "\(episodeCount) Episode" : "\(episodeCount) Episodes")
        }
        if let genres = item.genres, let firstGenre = genres.first {
            components.append(firstGenre)
        }
        return components.isEmpty ? nil : components.joined(separator: " · ")
    }

    private var formattedRating: String? {
        guard let rating = item.rating else { return nil }
        return String(format: "%.1f", rating)
    }

    public var body: some View {
        Button(action: onItemClicked) {
            HStack(alignment: .top, spacing: theme.spacing.medium) {
                LazyResizableImage(
                    url: item.posterUrl,
                    size: CGSize(width: 120, height: 200)
                ) { state in
                    if let image = state.image {
                        image
                            .resizable()
                            .aspectRatio(contentMode: .fill)
                    } else {
                        Rectangle()
                            .fill(theme.colors.surfaceVariant)
                    }
                }
                .frame(width: 120)
                .frame(maxHeight: .infinity)
                .clipped()

                VStack(alignment: .leading, spacing: theme.spacing.xSmall) {
                    Text(item.title)
                        .textStyle(theme.typography.titleMedium)
                        .foregroundColor(theme.colors.onSurface)
                        .lineLimit(2)

                    if let rating = formattedRating {
                        HStack(spacing: 4) {
                            Image(systemName: "star.fill")
                                .font(.system(size: 14))
                                .foregroundColor(theme.colors.accent)
                            Text(rating)
                                .textStyle(theme.typography.bodyMedium)
                                .foregroundColor(theme.colors.onSurface)
                        }
                    }

                    if let metadata = metadataText {
                        Text(metadata)
                            .textStyle(theme.typography.bodySmall)
                            .foregroundColor(theme.colors.onSurfaceVariant)
                            .lineLimit(3)
                    }

                    Spacer()

                    if !item.watchProviders.isEmpty {
                        HStack(spacing: 4) {
                            ForEach(Array(item.watchProviders.prefix(6))) { provider in
                                LazyResizableImage(
                                    url: provider.logoUrl,
                                    size: CGSize(width: 32, height: 32)
                                ) { state in
                                    if let image = state.image {
                                        image
                                            .resizable()
                                            .aspectRatio(contentMode: .fill)
                                    } else {
                                        Rectangle()
                                            .fill(theme.colors.surfaceVariant)
                                    }
                                }
                                .frame(width: 32, height: 32)
                                .clipShape(RoundedRectangle(cornerRadius: 6))
                            }
                        }
                    }
                }
                .frame(maxWidth: .infinity, alignment: .leading)
                .padding(.vertical, theme.spacing.small)
                .padding(.trailing, theme.spacing.small)
            }
            .frame(height: 200)
            .background(theme.colors.surfaceVariant)
            .clipShape(RoundedRectangle(cornerRadius: theme.shapes.large))
            .shadow(color: Color.black.opacity(0.1), radius: 4, x: 0, y: 2)
        }
        .buttonStyle(.plain)
    }
}

#Preview {
    VStack(spacing: 16) {
        LibraryListItemView(
            item: SwiftLibraryItem(
                traktId: 1,
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
                    SwiftProviders(providerId: 2, logoUrl: nil)
                ]
            ),
            onItemClicked: {}
        )

        LibraryListItemView(
            item: SwiftLibraryItem(
                traktId: 2,
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
