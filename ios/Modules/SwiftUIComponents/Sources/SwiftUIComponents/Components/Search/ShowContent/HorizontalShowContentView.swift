import SwiftUI

// MARK: - Main View

public struct HorizontalShowContentView: View {
    private let title: String
    private let chevronStyle: ChevronStyle
    private let items: [SwiftShow]
    private let onClick: (Int64) -> Void
    private let onMoreClicked: () -> Void
    private let spacing: CGFloat
    private let edgeInsets: EdgeInsets
    private let showEmptyState: Bool

    public init(
        title: String,
        chevronStyle: ChevronStyle = .none,
        items: [SwiftShow],
        spacing: CGFloat = 12,
        edgeInsets: EdgeInsets = EdgeInsets(top: 0, leading: 16, bottom: 0, trailing: 16),
        showEmptyState: Bool = false,
        onClick: @escaping (Int64) -> Void,
        onMoreClicked: @escaping () -> Void = {}
    ) {
        self.items = items
        self.title = title
        self.onClick = onClick
        self.chevronStyle = chevronStyle
        self.onMoreClicked = onMoreClicked
        self.spacing = spacing
        self.edgeInsets = edgeInsets
        self.showEmptyState = showEmptyState
    }

    public var body: some View {
        VStack(alignment: .leading, spacing: 8) {
            chevronView
            scrollContent
        }
    }

    // MARK: - Subviews

    @ViewBuilder
    private var chevronView: some View {
        ChevronTitle(
            title: title,
            chevronStyle: chevronStyle,
            action: onMoreClicked
        )
        .padding(.vertical, 8)
        .accessibilityAddTraits(.isHeader)
    }

    @ViewBuilder
    private var scrollContent: some View {
        ScrollView(.horizontal, showsIndicators: false) {
            HStack(spacing: spacing) {
                ForEach(items) { item in
                    ShowContentItemView(
                        title: item.title,
                        imageUrl: item.posterUrl
                    )
                    .contentShape(Rectangle())
                    .onTapGesture {
                        onClick(item.tmdbId)
                    }
                    .accessibilityElement(children: .combine)
                    .accessibilityLabel("\(item.title), tap to view details")
                }
            }
            .padding(edgeInsets)
        }
    }
}

// MARK: - Preview Provider

#Preview {
    VStack {
        HorizontalShowContentView(
            title: "Coming Soon",
            items: [
                .init(
                    tmdbId: 1234,
                    title: "Arcane",
                    posterUrl: "https://image.tmdb.org/t/p/w780/fqldf2t8ztc9aiwn3k6mlX3tvRT.jpg",
                    backdropUrl: nil,
                    inLibrary: false
                ),
                .init(
                    tmdbId: 123,
                    title: "The Lord of the Rings: The Rings of Power",
                    posterUrl: "https://image.tmdb.org/t/p/w780/NNC08YmJFFlLi1prBkK8quk3dp.jpg",
                    backdropUrl: nil,
                    inLibrary: false
                ),
                .init(
                    tmdbId: 12346,
                    title: "Kaos",
                    posterUrl: "https://image.tmdb.org/t/p/w780/9Piw6Zju39bn3enIDLZzPfjMTBR.jpg",
                    backdropUrl: nil,
                    inLibrary: false
                ),
            ],
            onClick: { _ in },
            onMoreClicked: {}
        )

        HorizontalShowContentView(
            title: "Trending Today",
            items: [
                .init(
                    tmdbId: 124,
                    title: "Terminator",
                    posterUrl: "https://image.tmdb.org/t/p/w780/woH18JkZMYhMSWqtHkPA4F6Gd1z.jpg",
                    backdropUrl: nil,
                    inLibrary: false
                ),
                .init(
                    tmdbId: 123_346,
                    title: "The Perfect Couple",
                    posterUrl: "https://image.tmdb.org/t/p/w780//3buRSGVnutw8x4Lww0t70k5dG6R.jpg",
                    backdropUrl: nil,
                    inLibrary: false
                ),
                .init(
                    tmdbId: 2346,
                    title: "One Piece",
                    posterUrl: "https://image.tmdb.org/t/p/w780/2rmK7mnchw9Xr3XdiTFSxTTLXqv.jpg",
                    backdropUrl: nil,
                    inLibrary: false
                ),
            ],
            onClick: { _ in },
            onMoreClicked: {}
        )
    }
}
