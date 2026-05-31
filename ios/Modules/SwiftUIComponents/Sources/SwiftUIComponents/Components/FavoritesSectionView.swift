import DesignSystem
import SwiftUI

public struct FavoritesSectionView: View {
    @Environment(\.appTheme) private var theme
    @Environment(\.widthSizeClass) private var widthSizeClass

    private var posterWidth: CGFloat {
        ImageType.poster.width(widthSizeClass)
    }

    private let favorites: SwiftSectionState<SwiftProfileShow>
    private let title: String
    private let retryLabel: String
    private let onShowClick: (Int64) -> Void
    private let onRetry: () -> Void

    public init(
        favorites: SwiftSectionState<SwiftProfileShow>,
        title: String,
        retryLabel: String,
        onShowClick: @escaping (Int64) -> Void,
        onRetry: @escaping () -> Void
    ) {
        self.favorites = favorites
        self.title = title
        self.retryLabel = retryLabel
        self.onShowClick = onShowClick
        self.onRetry = onRetry
    }

    public var body: some View {
        if case .empty = favorites {
            EmptyView()
        } else {
            CollapsibleSection(title: title) {
                body(for: favorites)
            }
        }
    }

    @ViewBuilder
    private func body(for sectionState: SwiftSectionState<SwiftProfileShow>) -> some View {
        switch sectionState {
        case .loading:
            ScrollView(.horizontal, showsIndicators: false) {
                HStack(spacing: theme.spacing.small) {
                    ForEach(0 ..< 3, id: \.self) { _ in
                        ShimmerView(cornerRadius: theme.shapes.medium)
                            .frame(width: posterWidth, height: posterWidth / ImageType.poster.aspect)
                    }
                }
                .padding(.horizontal, theme.spacing.medium)
            }
        case let .error(message):
            InlineSectionError(
                message: message,
                retryLabel: retryLabel,
                onRetry: onRetry
            )
        case let .content(shows):
            ScrollView(.horizontal, showsIndicators: false) {
                HStack(spacing: theme.spacing.small) {
                    ForEach(shows) { show in
                        Button(action: { onShowClick(show.id) }) {
                            PosterItemView(
                                title: show.title,
                                posterUrl: show.posterUrl,
                                posterWidth: posterWidth,
                                aspectRatio: ImageType.poster.aspect,
                                posterRadius: theme.shapes.medium
                            )
                        }
                        .buttonStyle(.plain)
                    }
                }
                .padding(.horizontal, theme.spacing.medium)
            }
        case .empty:
            EmptyView()
        }
    }
}

#Preview {
    FavoritesSectionView(
        favorites: .content([
            SwiftProfileShow(id: 1, title: "Breaking Bad", posterUrl: nil),
            SwiftProfileShow(id: 2, title: "Game of Thrones", posterUrl: nil),
        ]),
        title: "Favorites",
        retryLabel: "Retry",
        onShowClick: { _ in },
        onRetry: {}
    )
}
