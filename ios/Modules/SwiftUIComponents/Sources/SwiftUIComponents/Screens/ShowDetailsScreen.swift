import SwiftUI

public struct ShowDetailsScreen: View {
    @Theme private var appTheme

    private let title: String
    private let overview: String
    private let backdropImageUrl: String?
    private let posterImageUrl: String?
    private let status: String
    private let year: String
    private let language: String
    private let rating: Double
    private let isInLibrary: Bool
    private let isRefreshing: Bool
    private let openTrailersInYoutube: Bool
    private let selectedSeasonIndex: Int
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
    private let dayLabelFormat: (Int) -> String
    private let trackLabel: String
    private let stopTrackingLabel: String
    private let addToListLabel: String
    private let similarShowsTitle: String
    private let seasonDetailsTitle: String
    private let showSeasonDetailsHeader: Bool
    private let seasonCountFormat: (Int) -> String
    private let episodesWatchedFormat: (Int32, Int32) -> String
    private let episodesLeftFormat: (Int32) -> String
    private let upToDateLabel: String
    @Binding private var toast: Toast?
    private let onBack: () -> Void
    private let onRefresh: () -> Void
    private let onAddToCustomList: () -> Void
    private let onAddToLibrary: () -> Void
    private let onSeasonClicked: (Int, SwiftSeason) -> Void
    private let onShowClicked: (Int64) -> Void
    private let onMarkEpisodeWatched: (SwiftContinueTrackingEpisode) -> Void

    public init(
        title: String,
        overview: String,
        backdropImageUrl: String?,
        posterImageUrl: String?,
        status: String,
        year: String,
        language: String,
        rating: Double,
        isInLibrary: Bool,
        isRefreshing: Bool,
        openTrailersInYoutube: Bool,
        selectedSeasonIndex: Int,
        watchedEpisodesCount: Int32,
        totalEpisodesCount: Int32,
        genreList: [SwiftGenres],
        seasonList: [SwiftSeason],
        providerList: [SwiftProviders],
        trailerList: [SwiftTrailer],
        castsList: [SwiftCast],
        similarShows: [SwiftShow],
        continueTrackingEpisodes: [SwiftContinueTrackingEpisode],
        continueTrackingScrollIndex: Int,
        continueTrackingTitle: String,
        dayLabelFormat: @escaping (Int) -> String,
        trackLabel: String,
        stopTrackingLabel: String,
        addToListLabel: String,
        similarShowsTitle: String,
        seasonDetailsTitle: String,
        showSeasonDetailsHeader: Bool,
        seasonCountFormat: @escaping (Int) -> String,
        episodesWatchedFormat: @escaping (Int32, Int32) -> String,
        episodesLeftFormat: @escaping (Int32) -> String,
        upToDateLabel: String,
        toast: Binding<Toast?>,
        onBack: @escaping () -> Void,
        onRefresh: @escaping () -> Void,
        onAddToCustomList: @escaping () -> Void,
        onAddToLibrary: @escaping () -> Void,
        onSeasonClicked: @escaping (Int, SwiftSeason) -> Void,
        onShowClicked: @escaping (Int64) -> Void,
        onMarkEpisodeWatched: @escaping (SwiftContinueTrackingEpisode) -> Void
    ) {
        self.title = title
        self.overview = overview
        self.backdropImageUrl = backdropImageUrl
        self.posterImageUrl = posterImageUrl
        self.status = status
        self.year = year
        self.language = language
        self.rating = rating
        self.isInLibrary = isInLibrary
        self.isRefreshing = isRefreshing
        self.openTrailersInYoutube = openTrailersInYoutube
        self.selectedSeasonIndex = selectedSeasonIndex
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
        _toast = toast
        self.onBack = onBack
        self.onRefresh = onRefresh
        self.onAddToCustomList = onAddToCustomList
        self.onAddToLibrary = onAddToLibrary
        self.onSeasonClicked = onSeasonClicked
        self.onShowClicked = onShowClicked
        self.onMarkEpisodeWatched = onMarkEpisodeWatched
    }

    @State private var showGlass: Double = 0

    public var body: some View {
        ParallaxView(
            imageHeight: DimensionConstants.imageHeight,
            collapsedImageHeight: DimensionConstants.collapsedImageHeight,
            header: { proxy in
                HeaderView(
                    title: title,
                    overview: overview,
                    backdropImageUrl: backdropImageUrl,
                    status: status,
                    year: year,
                    language: language,
                    rating: rating,
                    seasonCount: seasonList.count,
                    seasonCountFormat: seasonCountFormat,
                    progress: proxy.getTitleOpacity(
                        geometry: proxy,
                        imageHeight: DimensionConstants.imageHeight,
                        collapsedImageHeight: DimensionConstants.collapsedImageHeight
                    ),
                    headerHeight: proxy.getHeightForHeaderImage(proxy)
                )
            },
            content: {
                ShowInfoView(
                    isFollowed: isInLibrary,
                    openTrailersInYoutube: openTrailersInYoutube,
                    selectedSeasonIndex: selectedSeasonIndex,
                    status: status,
                    watchedEpisodesCount: watchedEpisodesCount,
                    totalEpisodesCount: totalEpisodesCount,
                    genreList: genreList,
                    seasonList: seasonList,
                    providerList: providerList,
                    trailerList: trailerList,
                    castsList: castsList,
                    similarShows: similarShows,
                    continueTrackingEpisodes: continueTrackingEpisodes,
                    continueTrackingScrollIndex: continueTrackingScrollIndex,
                    continueTrackingTitle: continueTrackingTitle,
                    dayLabelFormat: dayLabelFormat,
                    trackLabel: trackLabel,
                    stopTrackingLabel: stopTrackingLabel,
                    addToListLabel: addToListLabel,
                    similarShowsTitle: similarShowsTitle,
                    seasonDetailsTitle: seasonDetailsTitle,
                    showSeasonDetailsHeader: showSeasonDetailsHeader,
                    seasonCountFormat: { count in seasonCountFormat(Int(count)) },
                    episodesWatchedFormat: episodesWatchedFormat,
                    episodesLeftFormat: episodesLeftFormat,
                    upToDateLabel: upToDateLabel,
                    onAddToCustomList: onAddToCustomList,
                    onAddToLibrary: onAddToLibrary,
                    onSeasonClicked: onSeasonClicked,
                    onShowClicked: onShowClicked,
                    onMarkEpisodeWatched: onMarkEpisodeWatched
                )
            },
            onScroll: { offset in
                let newValue = ParallaxConstants.glassOpacity(from: offset, triggerOffset: 170, divisor: 220)
                if abs(newValue - showGlass) > 0.02 {
                    showGlass = newValue
                }
            }
        )
        .background(appTheme.colors.background)
        .navigationBarTitleDisplayMode(.inline)
        .navigationBarColor(backgroundColor: .clear)
        .navigationBarBackButtonHidden(true)
        .swipeBackGesture(onSwipe: onBack)
        .overlay(
            GlassToolbar(
                title: title,
                opacity: showGlass,
                isLoading: isRefreshing,
                leadingIcon: {
                    GlassButton(icon: "chevron.left", action: onBack)
                        .opacity(1 - showGlass)
                },
                trailingIcon: {
                    GlassButton(icon: "arrow.clockwise", action: onRefresh)
                }
            )
            .animation(.easeInOut(duration: AnimationConstants.defaultDuration), value: showGlass),
            alignment: .top
        )
        .coordinateSpace(name: CoordinateSpaces.scrollView)
        .edgesIgnoringSafeArea(.top)
        .toastView(toast: $toast)
    }

    private enum CoordinateSpaces {
        case scrollView
    }
}

private enum DimensionConstants {
    static let imageHeight: CGFloat = 500
    static let collapsedImageHeight: CGFloat = 120.0
}
