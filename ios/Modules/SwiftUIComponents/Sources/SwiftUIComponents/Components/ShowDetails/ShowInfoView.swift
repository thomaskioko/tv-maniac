import SwiftUI

public struct ShowInfoView: View {
    @Theme private var theme
    @State private var toast: Toast?

    private let isFollowed: Bool
    private let openTrailersInYoutube: Bool
    private let selectedSeasonIndex: Int
    private let status: String?
    private let watchedEpisodesCount: Int32
    private let totalEpisodesCount: Int32
    private let genreList: [SwiftGenres]
    private let seasonList: [SwiftSeason]
    private let providerList: [SwiftProviders]
    private let trailerList: [SwiftTrailer]
    private let castsList: [SwiftCast]
    private let similarShows: [SwiftShow]
    private let continueTrackingEpisodes: [SwiftContinueTrackingEpisode]
    private let continueTrackingScrollIndex: Int
    private let continueTrackingTitle: String
    private let dayLabelFormat: (_ count: Int) -> String
    private let trackLabel: String
    private let stopTrackingLabel: String
    private let addToListLabel: String
    private let similarShowsTitle: String
    private let seasonDetailsTitle: String
    private let showSeasonDetailsHeader: Bool
    private let seasonCountFormat: (_ count: Int32) -> String
    private let episodesWatchedFormat: (_ watched: Int32, _ total: Int32) -> String
    private let episodesLeftFormat: (_ count: Int32) -> String
    private let upToDateLabel: String
    private let onAddToCustomList: () -> Void
    private let onAddToLibrary: () -> Void
    private let onSeasonClicked: (Int, SwiftSeason) -> Void
    private let onShowClicked: (Int64) -> Void
    private let onMarkEpisodeWatched: (SwiftContinueTrackingEpisode) -> Void

    public init(
        isFollowed: Bool,
        openTrailersInYoutube: Bool,
        selectedSeasonIndex: Int = 0,
        status: String?,
        watchedEpisodesCount: Int32,
        totalEpisodesCount: Int32,
        genreList: [SwiftGenres],
        seasonList: [SwiftSeason],
        providerList: [SwiftProviders],
        trailerList: [SwiftTrailer],
        castsList: [SwiftCast],
        similarShows: [SwiftShow],
        continueTrackingEpisodes: [SwiftContinueTrackingEpisode] = [],
        continueTrackingScrollIndex: Int = 0,
        continueTrackingTitle: String,
        dayLabelFormat: @escaping (_ count: Int) -> String,
        trackLabel: String,
        stopTrackingLabel: String,
        addToListLabel: String,
        similarShowsTitle: String,
        seasonDetailsTitle: String,
        showSeasonDetailsHeader: Bool = true,
        seasonCountFormat: @escaping (_ count: Int32) -> String,
        episodesWatchedFormat: @escaping (_ watched: Int32, _ total: Int32) -> String,
        episodesLeftFormat: @escaping (_ count: Int32) -> String,
        upToDateLabel: String,
        onAddToCustomList: @escaping () -> Void,
        onAddToLibrary: @escaping () -> Void,
        onSeasonClicked: @escaping (Int, SwiftSeason) -> Void,
        onShowClicked: @escaping (Int64) -> Void,
        onMarkEpisodeWatched: @escaping (SwiftContinueTrackingEpisode) -> Void = { _ in }
    ) {
        self.isFollowed = isFollowed
        self.openTrailersInYoutube = openTrailersInYoutube
        self.selectedSeasonIndex = selectedSeasonIndex
        self.status = status
        self.watchedEpisodesCount = watchedEpisodesCount
        self.totalEpisodesCount = totalEpisodesCount
        self.genreList = genreList
        self.seasonList = seasonList
        self.providerList = providerList
        self.trailerList = trailerList
        self.castsList = castsList
        self.similarShows = similarShows
        self.continueTrackingEpisodes = continueTrackingEpisodes
        self.continueTrackingScrollIndex = continueTrackingScrollIndex
        self.continueTrackingTitle = continueTrackingTitle
        self.dayLabelFormat = dayLabelFormat
        self.trackLabel = trackLabel
        self.stopTrackingLabel = stopTrackingLabel
        self.addToListLabel = addToListLabel
        self.similarShowsTitle = similarShowsTitle
        self.seasonDetailsTitle = seasonDetailsTitle
        self.showSeasonDetailsHeader = showSeasonDetailsHeader
        self.seasonCountFormat = seasonCountFormat
        self.episodesWatchedFormat = episodesWatchedFormat
        self.episodesLeftFormat = episodesLeftFormat
        self.upToDateLabel = upToDateLabel
        self.onAddToCustomList = onAddToCustomList
        self.onAddToLibrary = onAddToLibrary
        self.onSeasonClicked = onSeasonClicked
        self.onShowClicked = onShowClicked
        self.onMarkEpisodeWatched = onMarkEpisodeWatched
    }

    public var body: some View {
        VStack(spacing: theme.spacing.medium) {
            if !genreList.isEmpty {
                ScrollView(.horizontal, showsIndicators: false) {
                    HStack(alignment: .center, spacing: theme.spacing.xSmall) {
                        ForEach(genreList, id: \.name) { item in
                            ChipView(label: item.name)
                        }
                    }
                    .padding(.horizontal, theme.spacing.medium)
                }
            }

            HStack(alignment: .center, spacing: theme.spacing.xSmall) {
                watchlistButton
                listButton
            }

            ContinueTrackingSection(
                title: continueTrackingTitle,
                episodes: continueTrackingEpisodes,
                scrollIndex: continueTrackingScrollIndex,
                dayLabelFormat: dayLabelFormat,
                onMarkWatched: onMarkEpisodeWatched
            )

            SeasonProgressSection(
                title: seasonDetailsTitle,
                showHeader: showSeasonDetailsHeader,
                status: status,
                watchedEpisodesCount: watchedEpisodesCount,
                totalEpisodesCount: totalEpisodesCount,
                seasonsList: seasonList,
                selectedSeasonIndex: selectedSeasonIndex,
                seasonCountFormat: seasonCountFormat,
                episodesWatchedFormat: episodesWatchedFormat,
                episodesLeftFormat: episodesLeftFormat,
                upToDateLabel: upToDateLabel,
                onSeasonClicked: onSeasonClicked
            )

            ProviderListView(items: providerList)

            TrailerListView(
                trailers: trailerList,
                openInYouTube: openTrailersInYoutube,
                onError: { error in
                    toast = Toast(
                        type: .error,
                        title: "Error",
                        message: "Failed to play video: \(error.localizedDescription)",
                        duration: 3.5
                    )
                }
            )

            CastListView(casts: castsList)

            HorizontalItemListView(
                title: similarShowsTitle,
                items: similarShows,
                onClick: { id in onShowClicked(id) }
            )

        }
        .toastView(toast: $toast)
    }

    private var watchlistButton: some View {
        Button(action: onAddToLibrary) {
            VStack {
                if #available(iOS 17.0, *) {
                    Image(systemName: isFollowed ? "minus.circle.fill" : "plus.circle.fill")
                        .foregroundColor(theme.colors.onButtonBackground)
                        .symbolEffect(isFollowed ? .bounce.down : .bounce.up, value: isFollowed)
                } else {
                    Image(systemName: isFollowed ? "minus.circle.fill" : "plus.circle.fill")
                        .foregroundColor(theme.colors.onButtonBackground)
                }

                Text(isFollowed ? stopTrackingLabel : trackLabel)
                    .lineLimit(1)
                    .padding(.top, theme.spacing.xxxSmall)
                    .textStyle(theme.typography.labelSmall)
                    .foregroundColor(theme.colors.onButtonBackground)
            }
            .padding(.vertical, theme.spacing.xxSmall)
            .frame(width: DrawingConstants.buttonWidth, height: DrawingConstants.buttonHeight)
        }
        .buttonStyle(.borderedProminent)
        .controlSize(.small)
        .tint(isFollowed ? .red.opacity(0.95) : theme.colors.accent)
        .buttonBorderShape(.roundedRectangle(radius: DrawingConstants.buttonRadius))
    }

    private var listButton: some View {
        Button(action: onAddToCustomList) {
            VStack {
                Image(systemName: false ? "rectangle.on.rectangle.angled.fill" : "rectangle.on.rectangle.angled")
                    .foregroundColor(theme.colors.onButtonBackground)

                Text(addToListLabel)
                    .padding(.top, theme.spacing.xxxSmall)
                    .textStyle(theme.typography.labelSmall)
                    .foregroundColor(theme.colors.onButtonBackground)
                    .lineLimit(1)
            }
            .padding(.vertical, theme.spacing.xxSmall)
            .frame(width: DrawingConstants.buttonWidth, height: DrawingConstants.buttonHeight)
        }
        .buttonStyle(.borderedProminent)
        .controlSize(.small)
        .tint(theme.colors.accent)
        .buttonBorderShape(.roundedRectangle(radius: DrawingConstants.buttonRadius))
    }

    private enum DrawingConstants {
        static let buttonWidth: CGFloat = 85
        static let buttonHeight: CGFloat = 35
        static let buttonRadius: CGFloat = 12
    }
}

#Preview {
    VStack {
        Spacer(minLength: 520)

        ShowInfoView(
            isFollowed: true,
            openTrailersInYoutube: false,
            status: "Ended",
            watchedEpisodesCount: 7,
            totalEpisodesCount: 12,
            genreList: [
                .init(name: "Sci-Fi"),
                .init(name: "Horror"),
                .init(name: "Action"),
            ],
            seasonList: [
                .init(tvShowId: 23, seasonId: 23, seasonNumber: 1, name: "Season 1", watchedCount: 6, totalCount: 6, progressPercentage: 1.0),
                .init(tvShowId: 123, seasonId: 123, seasonNumber: 2, name: "Season 2", watchedCount: 1, totalCount: 6, progressPercentage: 0.17),
            ],
            providerList: [
                .init(
                    providerId: 123,
                    logoUrl: "https://image.tmdb.org/t/p/w780/https://image.tmdb.org/t/p/w780/4KAy34EHvRM25Ih8wb82AuGU7zJ.png"
                ),
                .init(
                    providerId: 1233,
                    logoUrl: "https://image.tmdb.org/t/p/w780/https://image.tmdb.org/t/p/w780/alqLicR1ZMHMaZGP3xRQxn9sq7p.png"
                ),
                .init(
                    providerId: 23,
                    logoUrl: "https://image.tmdb.org/t/p/w780/https://image.tmdb.org/t/p/w780/wwemzKWzjKYJFfCeiB57q3r4Bcm.png"
                ),
            ],
            trailerList: [
                .init(
                    showTmdbId: 123,
                    key: "XZ8daibM3AE",
                    name: "Series Trailer",
                    youtubeThumbnailUrl: "https://i.ytimg.com/vi/XZ8daibM3AE/hqdefault.jpg"
                ),
                .init(
                    showTmdbId: 1234,
                    key: "XZ8daibM3AE",
                    name: "Series Trailer",
                    youtubeThumbnailUrl: "https://i.ytimg.com/vi/XZ8daibM3AE/hqdefault.jpg"
                ),
            ],
            castsList: [
                .init(
                    castId: 123,
                    name: "Rosario Dawson",
                    characterName: "Claire Temple",
                    profileUrl: "https://image.tmdb.org/t/p/w780/1mm7JGHIUX3GRRGXEV9QCzsI0ao.jpg"
                ),
                .init(
                    castId: 1234,
                    name: "Hailee Steinfeld",
                    characterName: "Hailee Steinfeld",
                    profileUrl: "https://image.tmdb.org/t/p/w780/6aBclBl8GMcxbxr6XcwSGg3IBea.jpg"
                ),
                .init(
                    castId: 1235,
                    name: "内田夕夜",
                    characterName: "Yuuya Uchida",
                    profileUrl: "https://image.tmdb.org/t/p/w780/4xLLQGEDWtmLWUapo0UnfvCdsXp.jpg"
                ),
            ],
            similarShows: [
                .init(
                    traktId: 1234,
                    title: "Arcane",
                    posterUrl: "https://image.tmdb.org/t/p/w780/fqldf2t8ztc9aiwn3k6mlX3tvRT.jpg",
                    backdropUrl: nil,
                    inLibrary: false
                ),
                .init(
                    traktId: 123,
                    title: "The Lord of the Rings: The Rings of Power",
                    posterUrl: "https://image.tmdb.org/t/p/w780/NNC08YmJFFlLi1prBkK8quk3dp.jpg",
                    backdropUrl: nil,
                    inLibrary: false
                ),
                .init(
                    traktId: 12346,
                    title: "Kaos",
                    posterUrl: "https://image.tmdb.org/t/p/w780/9Piw6Zju39bn3enIDLZzPfjMTBR.jpg",
                    backdropUrl: nil,
                    inLibrary: false
                ),
            ],
            continueTrackingTitle: "Continue tracking",
            dayLabelFormat: { count in count == 1 ? "day" : "days" },
            trackLabel: "Track",
            stopTrackingLabel: "Stop Tracking",
            addToListLabel: "Add To List",
            similarShowsTitle: "Similar Shows",
            seasonDetailsTitle: "Season Details",
            seasonCountFormat: { count in count == 1 ? "\(count) Season" : "\(count) Seasons" },
            episodesWatchedFormat: { watched, total in "\(watched) of \(total) episodes watched" },
            episodesLeftFormat: { count in count == 1 ? "\(count) episode left to watch" : "\(count) episodes left to watch" },
            upToDateLabel: "You're up-to-date",
            onAddToCustomList: {},
            onAddToLibrary: {},
            onSeasonClicked: { _, _ in },
            onShowClicked: { _ in }
        )
    }
    .environment(\.tvManiacTheme, LightTheme())
}
