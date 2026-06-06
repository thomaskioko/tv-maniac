import DesignSystem
import Models
import SwiftUI

public enum CardStyle {
    case poster
    case backdrop
    case metallic
}

public struct HorizontalShowContentView: View {
    @Environment(\.appTheme) private var theme
    @Environment(\.widthSizeClass) private var widthSizeClass

    private let title: String
    private let subtitle: String?
    private let chevronStyle: ChevronStyle
    private let cardStyle: CardStyle
    private let items: [SwiftShow]
    private let onClick: (Int64) -> Void
    private let onMoreClicked: () -> Void
    private let spacing: CGFloat?
    private let edgeInsets: EdgeInsets?

    public init(
        title: String,
        subtitle: String? = nil,
        chevronStyle: ChevronStyle = .none,
        cardStyle: CardStyle = .metallic,
        items: [SwiftShow],
        spacing: CGFloat? = nil,
        edgeInsets: EdgeInsets? = nil,
        onClick: @escaping (Int64) -> Void,
        onMoreClicked: @escaping () -> Void = {}
    ) {
        self.title = title
        self.subtitle = subtitle
        self.chevronStyle = chevronStyle
        self.cardStyle = cardStyle
        self.items = items
        self.onClick = onClick
        self.onMoreClicked = onMoreClicked
        self.spacing = spacing
        self.edgeInsets = edgeInsets
    }

    public var body: some View {
        if !items.isEmpty {
            VStack(alignment: .leading, spacing: 0) {
                chevronView
                scrollContent
            }
        }
    }

    private var chevronView: some View {
        ChevronTitle(
            title: title,
            subtitle: subtitle,
            chevronStyle: chevronStyle,
            action: onMoreClicked
        )
        .padding(.vertical, theme.spacing.xSmall)
        .accessibilityAddTraits(.isHeader)
    }

    private var scrollContent: some View {
        ScrollView(.horizontal, showsIndicators: false) {
            HStack(spacing: spacing ?? theme.spacing.xSmall) {
                ForEach(items, id: \.showId) { item in
                    cardView(for: item)
                        .contentShape(Rectangle())
                        .onTapGesture { onClick(item.showId) }
                        .accessibilityElement(children: .combine)
                        .accessibilityLabel("\(item.title), tap to view details")
                }
            }
            .padding(edgeInsets ?? EdgeInsets(top: 0, leading: theme.spacing.medium, bottom: 0, trailing: 0))
        }
    }

    @ViewBuilder
    private func cardView(for item: SwiftShow) -> some View {
        switch cardStyle {
        case .poster:
            PosterItemView(
                title: item.title,
                posterUrl: item.posterUrl,
                isInLibrary: item.inLibrary,
                posterWidth: ImageType.poster.width(widthSizeClass),
                aspectRatio: ImageType.poster.aspect
            )
        case .backdrop:
            BackdropPosterCard(
                title: item.title,
                posterUrl: item.posterUrl,
                isInLibrary: item.inLibrary
            )
        case .metallic:
            ShowContentItemView(
                title: item.title,
                imageUrl: item.posterUrl
            )
        }
    }
}

#Preview {
    VStack {
        HorizontalShowContentView(
            title: "Trending Today",
            chevronStyle: .chevronOnly,
            cardStyle: .poster,
            items: [
                .init(showId: 1234, title: "Arcane", posterUrl: nil, backdropUrl: nil, inLibrary: false),
                .init(showId: 123, title: "The Rings of Power", posterUrl: nil, backdropUrl: nil, inLibrary: false),
            ],
            onClick: { _ in }
        )

        HorizontalShowContentView(
            title: "Coming Soon",
            cardStyle: .backdrop,
            items: [
                .init(showId: 12346, title: "Kaos", posterUrl: nil, backdropUrl: nil, inLibrary: false),
                .init(showId: 124, title: "Terminator", posterUrl: nil, backdropUrl: nil, inLibrary: false),
            ],
            onClick: { _ in }
        )

        HorizontalShowContentView(
            title: "Drama",
            subtitle: "Non-stop thrill and action",
            chevronStyle: .chevronOnly,
            items: [
                .init(showId: 2346, title: "One Piece", posterUrl: nil, backdropUrl: nil, inLibrary: false),
            ],
            onClick: { _ in }
        )
    }
}
