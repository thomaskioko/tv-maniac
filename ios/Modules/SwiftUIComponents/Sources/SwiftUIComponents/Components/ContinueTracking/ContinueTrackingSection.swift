import SwiftUI

public struct ContinueTrackingSection: View {
    @Theme private var theme

    private let title: String
    private let episodes: [SwiftContinueTrackingEpisode]
    private let scrollIndex: Int
    private let dayLabelFormat: (_ count: Int) -> String
    private let onMarkWatched: (SwiftContinueTrackingEpisode) -> Void

    public init(
        title: String,
        episodes: [SwiftContinueTrackingEpisode],
        scrollIndex: Int,
        dayLabelFormat: @escaping (_ count: Int) -> String,
        onMarkWatched: @escaping (SwiftContinueTrackingEpisode) -> Void
    ) {
        self.title = title
        self.episodes = episodes
        self.scrollIndex = scrollIndex
        self.dayLabelFormat = dayLabelFormat
        self.onMarkWatched = onMarkWatched
    }

    public var body: some View {
        if !episodes.isEmpty {
            VStack(alignment: .leading, spacing: theme.spacing.small) {
                Text(title)
                    .textStyle(theme.typography.titleMedium)
                    .foregroundColor(theme.colors.onSurface)
                    .padding(.horizontal, theme.spacing.medium)

                ScrollViewReader { proxy in
                    ScrollView(.horizontal, showsIndicators: false) {
                        HStack(spacing: theme.spacing.small) {
                            ForEach(episodes) { episode in
                                ContinueTrackingCard(
                                    episode: episode,
                                    dayLabelFormat: dayLabelFormat,
                                    onMarkWatched: { onMarkWatched(episode) }
                                )
                                .id(episode.id)
                            }
                        }
                        .padding(.horizontal, theme.spacing.medium)
                    }
                    .onAppear {
                        if scrollIndex >= 0, scrollIndex < episodes.count {
                            let targetId = episodes[scrollIndex].id
                            DispatchQueue.main.asyncAfter(deadline: .now() + 0.1) {
                                withAnimation(.easeInOut(duration: 0.3)) {
                                    proxy.scrollTo(targetId, anchor: .center)
                                }
                            }
                        }
                    }
                    .onChange(of: scrollIndex) { newIndex in
                        if newIndex >= 0, newIndex < episodes.count {
                            let targetId = episodes[newIndex].id
                            withAnimation(.easeInOut(duration: 0.3)) {
                                proxy.scrollTo(targetId, anchor: .center)
                            }
                        }
                    }
                }
            }
            .padding(.vertical, theme.spacing.medium)
        }
    }
}

#Preview {
    VStack {
        ContinueTrackingSection(
            title: "Continue tracking",
            episodes: [
                SwiftContinueTrackingEpisode(
                    episodeId: 1,
                    seasonId: 1,
                    showTraktId: 1,
                    episodeNumber: 1,
                    seasonNumber: 2,
                    episodeNumberFormatted: "S02 | E01 (E14)",
                    episodeTitle: "First Episode",
                    imageUrl: nil,
                    isWatched: true,
                    daysUntilAir: nil,
                    hasAired: true
                ),
                SwiftContinueTrackingEpisode(
                    episodeId: 2,
                    seasonId: 1,
                    showTraktId: 1,
                    episodeNumber: 2,
                    seasonNumber: 2,
                    episodeNumberFormatted: "S02 | E02 (E15)",
                    episodeTitle: "Second Episode",
                    imageUrl: nil,
                    isWatched: true,
                    daysUntilAir: nil,
                    hasAired: true
                ),
                SwiftContinueTrackingEpisode(
                    episodeId: 3,
                    seasonId: 1,
                    showTraktId: 1,
                    episodeNumber: 3,
                    seasonNumber: 2,
                    episodeNumberFormatted: "S02 | E03 (E16)",
                    episodeTitle: "Re:start",
                    imageUrl: nil,
                    isWatched: false,
                    daysUntilAir: nil,
                    hasAired: true
                ),
                SwiftContinueTrackingEpisode(
                    episodeId: 4,
                    seasonId: 1,
                    showTraktId: 1,
                    episodeNumber: 4,
                    seasonNumber: 2,
                    episodeNumberFormatted: "S02 | E04 (E17)",
                    episodeTitle: "Fourth Episode",
                    imageUrl: nil,
                    isWatched: false,
                    daysUntilAir: 5,
                    hasAired: false
                ),
            ],
            scrollIndex: 2,
            dayLabelFormat: { count in count == 1 ? "day" : "days" },
            onMarkWatched: { _ in }
        )
    }
}
