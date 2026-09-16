import Components
import DesignSystem
import Models
import SwiftUI
import TvManiacKit

public struct SearchResultsGridView: View {
    @Environment(\.appTheme) private var theme
    @Environment(\.widthSizeClass) private var widthSizeClass

    private let items: [SwiftSearchShow]
    private let onClick: (Int64) -> Void

    public init(
        items: [SwiftSearchShow],
        onClick: @escaping (Int64) -> Void
    ) {
        self.items = items
        self.onClick = onClick
    }

    public var body: some View {
        if !items.isEmpty {
            LazyVGrid(
                columns: ImageDimens.posterGridColumns(widthSizeClass, spacing: theme.spacing.small),
                spacing: theme.spacing.small
            ) {
                ForEach(items, id: \.showId) { item in
                    SearchResultGridItemView(item: item)
                        .contentShape(Rectangle())
                        .onTapGesture { onClick(item.showId) }
                        .accessibilityElement(children: .combine)
                        .accessibilityLabel("\(item.title), tap to view details")
                        .testTag(SearchTestTags.shared.resultItem(traktId: item.showId))
                }
            }
            .padding(.horizontal, theme.spacing.medium)
        }
    }
}

private struct SearchResultGridItemView: View {
    @Environment(\.appTheme) private var theme

    let item: SwiftSearchShow

    var body: some View {
        VStack(alignment: .leading, spacing: theme.spacing.xxSmall) {
            PosterItemView(
                title: item.title,
                posterUrl: item.imageUrl,
                isInLibrary: item.inLibrary
            )
            .aspectRatio(ImageDimens.posterAspect, contentMode: .fill)
            .frame(maxWidth: .infinity)
            .clipped()

            if !item.captionComponents.isEmpty {
                metadataText(item.captionComponents, accent: theme.colors.accent)
                    .textStyle(theme.typography.labelMedium)
                    .foregroundStyle(.appOnSurfaceVariant)
                    .lineLimit(1)
                    .truncationMode(.tail)
            }
        }
    }
}

#Preview {
    ScrollView {
        SearchResultsGridView(
            items: [
                .init(
                    tmdbId: 44234,
                    showId: 44234,
                    title: "The Penguin",
                    overview: nil,
                    status: "Ended",
                    imageUrl: "https://image.tmdb.org/t/p/w780/VSRmtRlYgd0pBISf7d34TAwWgB.jpg",
                    year: "2024",
                    voteAverage: 8.5,
                    inLibrary: true,
                    captionComponents: ["2024", "8 eps."]
                ),
                .init(
                    tmdbId: 1234,
                    showId: 1234,
                    title: "The Lord of the Rings: The Rings of Power",
                    overview: nil,
                    status: "Ended",
                    imageUrl: "https://image.tmdb.org/t/p/w780/NNC08YmJFFlLi1prBkK8quk3dp.jpg",
                    year: "2022",
                    voteAverage: 7.2,
                    inLibrary: false,
                    captionComponents: ["2024", "16 eps."]
                ),
                .init(
                    tmdbId: 124,
                    showId: 124,
                    title: "Kaos",
                    overview: nil,
                    status: "Ended",
                    imageUrl: "https://image.tmdb.org/t/p/w780/9Piw6Zju39bn3enIDLZzPfjMTBR.jpg",
                    year: nil,
                    voteAverage: 5.6,
                    inLibrary: false,
                    captionComponents: ["2024", "8 eps."]
                ),
            ],
            onClick: { _ in }
        )
    }
    .appPreview()
}
