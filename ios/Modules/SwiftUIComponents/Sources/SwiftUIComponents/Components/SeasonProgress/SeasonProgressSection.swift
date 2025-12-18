import SwiftUI

public struct SeasonProgressSection: View {
    @Theme private var theme

    private let title: String
    private let showHeader: Bool
    private let status: String?
    private let watchedEpisodesCount: Int32
    private let totalEpisodesCount: Int32
    private let seasonsList: [SwiftSeason]
    private let selectedSeasonIndex: Int
    private let seasonCountFormat: (_ count: Int32) -> String
    private let episodesWatchedFormat: (_ watched: Int32, _ total: Int32) -> String
    private let episodesLeftFormat: (_ count: Int32) -> String
    private let upToDateLabel: String
    private let onSeasonClicked: (Int, SwiftSeason) -> Void

    public init(
        title: String,
        showHeader: Bool = true,
        status: String?,
        watchedEpisodesCount: Int32,
        totalEpisodesCount: Int32,
        seasonsList: [SwiftSeason],
        selectedSeasonIndex: Int = 0,
        seasonCountFormat: @escaping (_ count: Int32) -> String,
        episodesWatchedFormat: @escaping (_ watched: Int32, _ total: Int32) -> String,
        episodesLeftFormat: @escaping (_ count: Int32) -> String,
        upToDateLabel: String,
        onSeasonClicked: @escaping (Int, SwiftSeason) -> Void
    ) {
        self.title = title
        self.showHeader = showHeader
        self.status = status
        self.watchedEpisodesCount = watchedEpisodesCount
        self.totalEpisodesCount = totalEpisodesCount
        self.seasonsList = seasonsList
        self.selectedSeasonIndex = selectedSeasonIndex
        self.seasonCountFormat = seasonCountFormat
        self.episodesWatchedFormat = episodesWatchedFormat
        self.episodesLeftFormat = episodesLeftFormat
        self.upToDateLabel = upToDateLabel
        self.onSeasonClicked = onSeasonClicked
    }

    private var remainingEpisodes: Int32 {
        totalEpisodesCount - watchedEpisodesCount
    }

    private var isUpToDate: Bool {
        remainingEpisodes <= 0 && totalEpisodesCount > 0
    }

    private var seasonCount: Int32 {
        Int32(seasonsList.count)
    }

    public var body: some View {
        if seasonsList.isEmpty {
            EmptyView()
        } else {
            VStack(alignment: .leading, spacing: theme.spacing.small) {
                if showHeader {
                    Text(title)
                        .textStyle(theme.typography.titleSmall)
                        .foregroundColor(theme.colors.onSurface)
                        .padding(.horizontal, theme.spacing.medium)
                }

                VStack(alignment: .leading, spacing: theme.spacing.small) {
                    headerView
                    progressTexts
                    progressBar
                    seasonsScrollView
                }
                .padding(theme.spacing.medium)
                .background(theme.colors.surface)
                .clipShape(RoundedRectangle(cornerRadius: theme.shapes.medium))
                .padding(.horizontal, theme.spacing.medium)
            }
            .padding(.vertical, theme.spacing.small)
        }
    }

    private var headerView: some View {
        HStack(spacing: 0) {
            if let status {
                Text(status)
                    .textStyle(theme.typography.titleSmall)
                    .foregroundColor(theme.colors.onSurface)
                    .fontWeight(.bold)

                Text(" · ")
                    .textStyle(theme.typography.titleSmall)
                    .foregroundColor(theme.colors.accent)
            }

            Text(seasonCountFormat(seasonCount))
                .textStyle(theme.typography.titleSmall)
                .foregroundColor(theme.colors.onSurface)
                .fontWeight(.bold)
        }
    }

    private var progressTexts: some View {
        VStack(alignment: .leading, spacing: theme.spacing.xxxSmall) {
            Text(episodesWatchedFormat(watchedEpisodesCount, totalEpisodesCount))
                .textStyle(theme.typography.bodyMedium)
                .foregroundColor(theme.colors.onSurfaceVariant)

            Text(isUpToDate ? upToDateLabel : episodesLeftFormat(remainingEpisodes))
                .textStyle(theme.typography.bodySmall)
                .foregroundColor(theme.colors.onSurfaceVariant)
        }
    }

    private var progressBar: some View {
        SegmentedProgressBar(
            segmentProgress: seasonsList.map(\.progressPercentage)
        )
        .padding(.vertical, theme.spacing.xSmall)
    }

    private var seasonsScrollView: some View {
        ScrollViewReader { proxy in
            ScrollView(.horizontal, showsIndicators: false) {
                HStack(spacing: theme.spacing.xSmall) {
                    ForEach(Array(seasonsList.enumerated()), id: \.element.id) { index, season in
                        SeasonProgressCard(
                            season: season,
                            isSelected: index == selectedSeasonIndex,
                            onClick: { onSeasonClicked(index, season) }
                        )
                        .id(index)
                    }
                }
            }
            .onAppear {
                if selectedSeasonIndex > 0, selectedSeasonIndex < seasonsList.count {
                    proxy.scrollTo(selectedSeasonIndex, anchor: .center)
                }
            }
        }
    }
}

#Preview("Partial Progress") {
    SeasonProgressSection(
        title: "Season Details",
        status: "Ended",
        watchedEpisodesCount: 7,
        totalEpisodesCount: 12,
        seasonsList: [
            SwiftSeason(
                tvShowId: 1,
                seasonId: 1,
                seasonNumber: 1,
                name: "Season 1",
                watchedCount: 6,
                totalCount: 6,
                progressPercentage: 1.0
            ),
            SwiftSeason(
                tvShowId: 1,
                seasonId: 2,
                seasonNumber: 2,
                name: "Season 2",
                watchedCount: 1,
                totalCount: 6,
                progressPercentage: 0.17
            ),
        ],
        seasonCountFormat: { count in count == 1 ? "\(count) Season" : "\(count) Seasons" },
        episodesWatchedFormat: { watched, total in "\(watched) of \(total) episodes watched" },
        episodesLeftFormat: { count in count == 1 ? "\(count) episode left to watch" : "\(count) episodes left to watch" },
        upToDateLabel: "You're up-to-date",
        onSeasonClicked: { _, _ in }
    )
    .environment(\.tvManiacTheme, DarkTheme())
}

#Preview("Up To Date") {
    SeasonProgressSection(
        title: "Season Details",
        status: "Returning Series",
        watchedEpisodesCount: 30,
        totalEpisodesCount: 30,
        seasonsList: [
            SwiftSeason(tvShowId: 1, seasonId: 1, seasonNumber: 1, name: "Season 1", watchedCount: 6, totalCount: 6, progressPercentage: 1.0),
            SwiftSeason(tvShowId: 1, seasonId: 2, seasonNumber: 2, name: "Season 2", watchedCount: 6, totalCount: 6, progressPercentage: 1.0),
            SwiftSeason(tvShowId: 1, seasonId: 3, seasonNumber: 3, name: "Season 3", watchedCount: 6, totalCount: 6, progressPercentage: 1.0),
            SwiftSeason(tvShowId: 1, seasonId: 4, seasonNumber: 4, name: "Season 4", watchedCount: 6, totalCount: 6, progressPercentage: 1.0),
            SwiftSeason(tvShowId: 1, seasonId: 5, seasonNumber: 5, name: "Season 5", watchedCount: 6, totalCount: 6, progressPercentage: 1.0),
        ],
        seasonCountFormat: { count in count == 1 ? "\(count) Season" : "\(count) Seasons" },
        episodesWatchedFormat: { watched, total in "\(watched) of \(total) episodes watched" },
        episodesLeftFormat: { count in count == 1 ? "\(count) episode left to watch" : "\(count) episodes left to watch" },
        upToDateLabel: "You're up-to-date",
        onSeasonClicked: { _, _ in }
    )
    .environment(\.tvManiacTheme, DarkTheme())
}

#Preview("Untracked") {
    SeasonProgressSection(
        title: "Season Details",
        status: "Ended",
        watchedEpisodesCount: 0,
        totalEpisodesCount: 12,
        seasonsList: [
            SwiftSeason(tvShowId: 1, seasonId: 1, seasonNumber: 1, name: "Season 1", watchedCount: 0, totalCount: 6, progressPercentage: 0),
            SwiftSeason(tvShowId: 1, seasonId: 2, seasonNumber: 2, name: "Season 2", watchedCount: 0, totalCount: 6, progressPercentage: 0),
        ],
        seasonCountFormat: { count in count == 1 ? "\(count) Season" : "\(count) Seasons" },
        episodesWatchedFormat: { watched, total in "\(watched) of \(total) episodes watched" },
        episodesLeftFormat: { count in count == 1 ? "\(count) episode left to watch" : "\(count) episodes left to watch" },
        upToDateLabel: "You're up-to-date",
        onSeasonClicked: { _, _ in }
    )
    .environment(\.tvManiacTheme, DarkTheme())
}
