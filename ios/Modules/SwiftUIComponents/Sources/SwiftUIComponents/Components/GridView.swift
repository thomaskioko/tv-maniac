import SwiftUI

public struct GridView: View {
    @Theme private var theme

    private let posterWidth: CGFloat
    private let posterHeight: CGFloat
    private let spacing: CGFloat?
    private let columns: [GridItem]
    private let items: [ShowPosterImage]
    private var onAction: (Int64) -> Void

    public init(
        items: [ShowPosterImage],
        posterWidth: CGFloat = 130,
        posterHeight: CGFloat = 200,
        spacing: CGFloat? = nil,
        columns: [GridItem] = [GridItem(.adaptive(minimum: 100), spacing: 4)],
        onAction: @escaping (Int64) -> Void
    ) {
        self.posterWidth = posterWidth
        self.posterHeight = posterHeight
        self.spacing = spacing
        self.columns = columns
        self.items = items
        self.onAction = onAction
    }

    public var body: some View {
        ScrollView(.vertical, showsIndicators: false) {
            LazyVGrid(columns: columns, spacing: spacing ?? theme.spacing.xxSmall) {
                ForEach(items, id: \.tmdbId) { item in
                    PosterItemView(
                        title: item.title,
                        posterUrl: item.posterUrl,
                        posterWidth: posterWidth,
                        posterHeight: posterHeight
                    )
                    .aspectRatio(contentMode: .fill)
                    .frame(minWidth: 0, maxWidth: .infinity, minHeight: 0, maxHeight: .infinity)
                    .clipped()
                    .onTapGesture { onAction(item.tmdbId) }
                }
            }.padding(.all, theme.spacing.xSmall)
        }
    }
}

#Preview {
    GridView(
        items: [
            .init(
                tmdbId: 1234,
                title: "Arcane",
                posterUrl: "https://image.tmdb.org/t/p/w780/fqldf2t8ztc9aiwn3k6mlX3tvRT.jpg"
            ),
            .init(
                tmdbId: 123,
                title: "The Lord of the Rings: The Rings of Power",
                posterUrl: "https://image.tmdb.org/t/p/w780/NNC08YmJFFlLi1prBkK8quk3dp.jpg"
            ),
            .init(
                tmdbId: 12346,
                title: "Kaos",
                posterUrl: "https://image.tmdb.org/t/p/w780/9Piw6Zju39bn3enIDLZzPfjMTBR.jpg"
            ),
            .init(
                tmdbId: 124,
                title: "Terminator",
                posterUrl: "https://image.tmdb.org/t/p/w780/woH18JkZMYhMSWqtHkPA4F6Gd1z.jpg"
            ),
            .init(
                tmdbId: 123_346,
                title: "The Perfect Couple",
                posterUrl: "https://image.tmdb.org/t/p/w780//3buRSGVnutw8x4Lww0t70k5dG6R.jpg"
            ),
            .init(
                tmdbId: 2346,
                title: "One Piece",
                posterUrl: "https://image.tmdb.org/t/p/w780/2rmK7mnchw9Xr3XdiTFSxTTLXqv.jpg"
            ),
        ],
        onAction: { _ in }
    )
}
