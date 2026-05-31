import DesignSystem
import SwiftUI

public struct RecentlyWatchedSectionView: View {
    @Environment(\.appTheme) private var theme

    private let recentlyWatched: SwiftSectionState<SwiftProfileRecentShow>
    private let title: String
    private let retryLabel: String
    private let onShowClick: (Int64) -> Void
    private let onRetry: () -> Void

    public init(
        recentlyWatched: SwiftSectionState<SwiftProfileRecentShow>,
        title: String,
        retryLabel: String,
        onShowClick: @escaping (Int64) -> Void,
        onRetry: @escaping () -> Void
    ) {
        self.recentlyWatched = recentlyWatched
        self.title = title
        self.retryLabel = retryLabel
        self.onShowClick = onShowClick
        self.onRetry = onRetry
    }

    public var body: some View {
        if case .empty = recentlyWatched {
            EmptyView()
        } else {
            CollapsibleSection(title: title) {
                body(for: recentlyWatched)
            }
        }
    }

    @ViewBuilder
    private func body(for sectionState: SwiftSectionState<SwiftProfileRecentShow>) -> some View {
        switch sectionState {
        case .loading:
            ScrollView(.horizontal, showsIndicators: false) {
                HStack(alignment: .top, spacing: theme.spacing.small) {
                    ForEach(0 ..< 3, id: \.self) { _ in
                        VStack(alignment: .leading, spacing: theme.spacing.xSmall) {
                            ShimmerView(cornerRadius: theme.shapes.medium)
                                .frame(width: 120, height: 180)
                            ShimmerView(cornerRadius: theme.shapes.small)
                                .frame(width: 120, height: 14)
                            ShimmerView(cornerRadius: theme.shapes.small)
                                .frame(width: 60, height: 12)
                        }
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
                HStack(alignment: .top, spacing: theme.spacing.small) {
                    ForEach(shows) { show in
                        Button(action: { onShowClick(show.traktId) }) {
                            episodeCard(show)
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

    private func episodeCard(_ show: SwiftProfileRecentShow) -> some View {
        VStack(alignment: .leading) {
            PosterItemView(
                title: show.title,
                posterUrl: show.posterUrl,
                posterWidth: 120,
                aspectRatio: 2.0 / 3.0,
                posterRadius: theme.shapes.medium
            )
            .padding(.top, theme.spacing.xxSmall)

            Text(show.title)
                .textStyle(theme.typography.labelMedium)
                .foregroundStyle(theme.colors.onSurface)
                .lineLimit(1)

            Text(show.episodeLabel)
                .textStyle(theme.typography.bodySmall)
                .foregroundStyle(theme.colors.onSurfaceVariant)
                .lineLimit(1)
        }
        .frame(width: 120, alignment: .leading)
    }
}

#Preview {
    RecentlyWatchedSectionView(
        recentlyWatched: .content([
            SwiftProfileRecentShow(traktId: 1, title: "Breaking Bad", posterUrl: nil, episodeLabel: "S5E14"),
            SwiftProfileRecentShow(traktId: 2, title: "Game of Thrones", posterUrl: nil, episodeLabel: "S8E3"),
        ]),
        title: "Recently Watched",
        retryLabel: "Retry",
        onShowClick: { _ in },
        onRetry: {}
    )
}
