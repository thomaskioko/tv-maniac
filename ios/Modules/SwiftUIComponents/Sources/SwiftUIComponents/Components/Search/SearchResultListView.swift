import SwiftUI

public struct SearchResultListView: View {
    @Theme private var theme

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
            ScrollView(.vertical, showsIndicators: false) {
                LazyVStack(spacing: theme.spacing.xSmall) {
                    ForEach(items, id: \.tmdbId) { item in
                        SearchItemView(
                            title: item.title,
                            overview: item.overview,
                            imageUrl: item.imageUrl,
                            status: item.status,
                            year: item.year,
                            voteAverage: item.voteAverage
                        )
                        .padding(.horizontal, theme.spacing.xSmall)
                        .onTapGesture {
                            onClick(item.traktId)
                        }
                    }
                }
            }
        }
    }
}

#Preview {
    VStack {
        SearchResultListView(
            items: [
                .init(
                    tmdbId: 44234,
                    traktId: 44234,
                    title: "The Penguin",
                    overview: "Follow Oswald Oz Cobb’s quest for control as he seeks to fill the power vacuum left by the death of Carmine Falcone, transforming from a disfigured nobody to a noted mobster in Gotham City.",
                    status: "Ended",
                    imageUrl: "https://image.tmdb.org/t/p/w780/VSRmtRlYgd0pBISf7d34TAwWgB.jpg",
                    year: "2015",
                    voteAverage: 5.6
                ),
                .init(
                    tmdbId: 1234,
                    traktId: 44234,
                    title: "The Lord of the Rings: The Rings of Power",
                    overview: "In 1997, a haunted scientist brushes his family aside for an all-consuming project. In 2022, a renegade fighter battles a powerful robot for vital data.",
                    status: "Ended",
                    imageUrl: "https://image.tmdb.org/t/p/w780/NNC08YmJFFlLi1prBkK8quk3dp.jpg",
                    year: "2015",
                    voteAverage: 5.6
                ),
                .init(
                    tmdbId: 124,
                    traktId: 44234,
                    title: "Kaos",
                    overview: "In 1997, a haunted scientist brushes his family aside for an all-consuming project. In 2022, a renegade fighter battles a powerful robot for vital data.",
                    status: "Ended",
                    imageUrl: "https://image.tmdb.org/t/p/w780/9Piw6Zju39bn3enIDLZzPfjMTBR.jpg",
                    year: "2015",
                    voteAverage: 5.6
                ),
                .init(
                    tmdbId: 234,
                    traktId: 44234,
                    title: "Terminator",
                    overview: "In 1997, a haunted scientist brushes his family aside for an all-consuming project. In 2022, a renegade fighter battles a powerful robot for vital data.",
                    status: "Ended",
                    imageUrl: "https://image.tmdb.org/t/p/w780/woH18JkZMYhMSWqtHkPA4F6Gd1z.jpg",
                    year: "2015",
                    voteAverage: 5.6
                ),
            ],
            onClick: { _ in }
        )
    }
}
