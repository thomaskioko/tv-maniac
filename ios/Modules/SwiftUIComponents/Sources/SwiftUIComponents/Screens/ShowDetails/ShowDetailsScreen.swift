import SwiftUI

public struct ShowDetailsScreen: View {
    public struct State {
        public let title: String
        public let overview: String
        public let backdropImageUrl: String?
        public let posterImageUrl: String?
        public let status: String
        public let year: String
        public let language: String
        public let rating: Double
        public let isInLibrary: Bool
        public let isRefreshing: Bool
        public let openTrailersInYoutube: Bool
        public let selectedSeasonIndex: Int
        public let watchedEpisodesCount: Int32
        public let totalEpisodesCount: Int32
        public let genreList: [SwiftGenres]
        public let seasonList: [SwiftSeason]
        public let providerList: [SwiftProviders]
        public let trailerList: [SwiftTrailer]
        public let castsList: [SwiftCast]
        public let similarShows: [SwiftShow]
        public let continueTrackingEpisodes: [SwiftContinueTrackingEpisode]
        public let continueTrackingScrollIndex: Int
        public let continueTrackingTitle: String
        public let tbdLabel: String
        public let trackLabel: String
        public let stopTrackingLabel: String
        public let addToListLabel: String
        public let similarShowsTitle: String
        public let seasonDetailsTitle: String
        public let showSeasonDetailsHeader: Bool
        public let upToDateLabel: String

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
            tbdLabel: String,
            trackLabel: String,
            stopTrackingLabel: String,
            addToListLabel: String,
            similarShowsTitle: String,
            seasonDetailsTitle: String,
            showSeasonDetailsHeader: Bool,
            upToDateLabel: String
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
            self.tbdLabel = tbdLabel
            self.trackLabel = trackLabel
            self.stopTrackingLabel = stopTrackingLabel
            self.addToListLabel = addToListLabel
            self.similarShowsTitle = similarShowsTitle
            self.seasonDetailsTitle = seasonDetailsTitle
            self.showSeasonDetailsHeader = showSeasonDetailsHeader
            self.upToDateLabel = upToDateLabel
        }
    }

    @Theme private var appTheme

    private let state: State
    private let dayLabelFormat: (Int) -> String
    private let seasonCountFormat: (Int) -> String
    private let episodesWatchedFormat: (Int32, Int32) -> String
    private let episodesLeftFormat: (Int32) -> String
    @Binding private var toast: Toast?
    private let onBack: () -> Void
    private let onRefresh: () -> Void
    private let onAddToCustomList: () -> Void
    private let onAddToLibrary: () -> Void
    private let onSeasonClicked: (Int, SwiftSeason) -> Void
    private let onShowClicked: (Int64) -> Void
    private let onMarkEpisodeWatched: (SwiftContinueTrackingEpisode) -> Void

    public init(
        state: State,
        dayLabelFormat: @escaping (Int) -> String,
        seasonCountFormat: @escaping (Int) -> String,
        episodesWatchedFormat: @escaping (Int32, Int32) -> String,
        episodesLeftFormat: @escaping (Int32) -> String,
        toast: Binding<Toast?>,
        onBack: @escaping () -> Void,
        onRefresh: @escaping () -> Void,
        onAddToCustomList: @escaping () -> Void,
        onAddToLibrary: @escaping () -> Void,
        onSeasonClicked: @escaping (Int, SwiftSeason) -> Void,
        onShowClicked: @escaping (Int64) -> Void,
        onMarkEpisodeWatched: @escaping (SwiftContinueTrackingEpisode) -> Void
    ) {
        self.state = state
        self.dayLabelFormat = dayLabelFormat
        self.seasonCountFormat = seasonCountFormat
        self.episodesWatchedFormat = episodesWatchedFormat
        self.episodesLeftFormat = episodesLeftFormat
        _toast = toast
        self.onBack = onBack
        self.onRefresh = onRefresh
        self.onAddToCustomList = onAddToCustomList
        self.onAddToLibrary = onAddToLibrary
        self.onSeasonClicked = onSeasonClicked
        self.onShowClicked = onShowClicked
        self.onMarkEpisodeWatched = onMarkEpisodeWatched
    }

    @SwiftUI.State private var showGlass: Double = 0

    public var body: some View {
        ParallaxView(
            imageHeight: DimensionConstants.imageHeight,
            collapsedImageHeight: DimensionConstants.collapsedImageHeight,
            header: { proxy in
                HeaderView(
                    title: state.title,
                    overview: state.overview,
                    backdropImageUrl: state.backdropImageUrl,
                    status: state.status,
                    year: state.year,
                    language: state.language,
                    rating: state.rating,
                    seasonCount: state.seasonList.count,
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
                    isFollowed: state.isInLibrary,
                    openTrailersInYoutube: state.openTrailersInYoutube,
                    selectedSeasonIndex: state.selectedSeasonIndex,
                    status: state.status,
                    watchedEpisodesCount: state.watchedEpisodesCount,
                    totalEpisodesCount: state.totalEpisodesCount,
                    genreList: state.genreList,
                    seasonList: state.seasonList,
                    providerList: state.providerList,
                    trailerList: state.trailerList,
                    castsList: state.castsList,
                    similarShows: state.similarShows,
                    continueTrackingEpisodes: state.continueTrackingEpisodes,
                    continueTrackingScrollIndex: state.continueTrackingScrollIndex,
                    continueTrackingTitle: state.continueTrackingTitle,
                    dayLabelFormat: dayLabelFormat,
                    tbdLabel: state.tbdLabel,
                    trackLabel: state.trackLabel,
                    stopTrackingLabel: state.stopTrackingLabel,
                    addToListLabel: state.addToListLabel,
                    similarShowsTitle: state.similarShowsTitle,
                    seasonDetailsTitle: state.seasonDetailsTitle,
                    showSeasonDetailsHeader: state.showSeasonDetailsHeader,
                    seasonCountFormat: { count in seasonCountFormat(Int(count)) },
                    episodesWatchedFormat: episodesWatchedFormat,
                    episodesLeftFormat: episodesLeftFormat,
                    upToDateLabel: state.upToDateLabel,
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
                title: state.title,
                opacity: showGlass,
                isLoading: state.isRefreshing,
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
