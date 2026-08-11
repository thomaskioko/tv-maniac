import Components
import DesignSystem
import SwiftUI

private let scrimHeight: CGFloat = 96
private let backdropAspect: CGFloat = 16.0 / 9.0

public struct LibraryDetailedCardView: View {
    @Environment(\.appTheme) private var theme

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
            ZStack(alignment: .bottomLeading) {
                Color.clear
                    .aspectRatio(backdropAspect, contentMode: .fit)
                    .overlay { LazyResizableImage(url: item.posterUrl) }
                    .clipped()

                LinearGradient(
                    colors: [
                        .clear,
                        theme.colors.surface.opacity(0.4),
                        theme.colors.surface.opacity(0.7),
                        theme.colors.surface.opacity(0.9),
                        theme.colors.surface,
                    ],
                    startPoint: .top,
                    endPoint: .bottom
                )
                .frame(height: scrimHeight)
                .frame(maxWidth: .infinity, alignment: .bottom)

                VStack(alignment: .leading, spacing: theme.spacing.xxSmall) {
                    Text(item.title)
                        .textStyle(theme.typography.titleMedium)
                        .foregroundStyle(.appOnSurface)
                        .lineLimit(1)

                    if !item.metadataComponents.isEmpty {
                        metadataText(item.metadataComponents, accent: theme.colors.accent)
                            .textStyle(theme.typography.bodySmall)
                            .foregroundStyle(.appOnSurfaceVariant)
                            .lineLimit(1)
                    }
                }
                .padding(theme.spacing.medium)
            }
            .clipShape(RoundedRectangle(cornerRadius: theme.shapes.medium, style: .continuous))
            .appShadow(theme.shadows.medium)
        }
        .buttonStyle(.plain)
    }
}

#Preview {
    ScrollView {
        VStack(spacing: TvManiacSpacingScheme.default.medium) {
            LibraryDetailedCardView(
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

            LibraryDetailedCardView(
                item: SwiftLibraryItem(
                    showId: 2,
                    title: "Game of Thrones",
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
}
