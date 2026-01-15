import SwiftUI
import SwiftUIComponents
import TvManiacKit

struct ShowDetailsView: View {
    @Theme private var theme

    private let presenter: ShowDetailsPresenter
    @StateObject @KotlinStateFlow private var uiState: ShowDetailsContent
    @State private var showGlass: Double = 0
    @State private var showCustomList = false

    init(presenter: ShowDetailsPresenter) {
        self.presenter = presenter
        _uiState = .init(presenter.state)
    }

    var body: some View {
        ParallaxView(
            imageHeight: DimensionConstants.imageHeight,
            collapsedImageHeight: DimensionConstants.collapsedImageHeight,
            header: { proxy in
                HeaderView(
                    title: uiState.showDetails.title,
                    overview: uiState.showDetails.overview,
                    backdropImageUrl: uiState.showDetails.backdropImageUrl,
                    status: uiState.showDetails.status,
                    year: uiState.showDetails.year,
                    language: uiState.showDetails.language,
                    rating: uiState.showDetails.rating,
                    seasonCount: Int(uiState.showDetails.seasonsList.count),
                    seasonCountFormat: { count in String(\.season_count, quantity: count) },
                    progress: proxy.getTitleOpacity(
                        geometry: proxy,
                        imageHeight: DimensionConstants.imageHeight,
                        collapsedImageHeight: DimensionConstants.collapsedImageHeight
                    ),
                    headerHeight: proxy.getHeightForHeaderImage(proxy)
                )
            },
            content: {
                showInfoDetails
            },
            onScroll: { offset in
                let opacity = -offset - 170
                let normalizedOpacity = opacity / 220
                showGlass = max(0, min(1, normalizedOpacity))
            }
        )
        .background(theme.colors.background)
        .navigationBarTitleDisplayMode(.inline)
        .navigationBarColor(backgroundColor: .clear)
        .navigationBarBackButtonHidden(true)
        .swipeBackGesture {
            presenter.dispatch(action: DetailBackClicked())
        }
        .overlay(
            GlassToolbar(
                title: uiState.showDetails.title,
                opacity: showGlass,
                isLoading: uiState.isRefreshing,
                leadingIcon: {
                    Button(action: {
                        presenter.dispatch(action: DetailBackClicked())
                    }) {
                        Image(systemName: "chevron.left")
                            .foregroundColor(theme.colors.accent)
                            .imageScale(.large)
                            .opacity(1 - showGlass)
                    }
                }
            ),
            alignment: .top
        )
        .animation(.easeInOut(duration: 0.25), value: showGlass)
        .coordinateSpace(name: CoordinateSpaces.scrollView)
        .edgesIgnoringSafeArea(.top)
        .sheet(isPresented: $showCustomList) {
            WatchlistSelector(
                showView: $showCustomList,
                title: uiState.showDetails.title,
                posterUrl: uiState.showDetails.posterImageUrl
            )
        }
    }

    @ViewBuilder
    private var showInfoDetails: some View {
        ShowInfoView(
            isFollowed: uiState.showDetails.isInLibrary,
            openTrailersInYoutube: uiState.showDetails.hasWebViewInstalled,
            selectedSeasonIndex: Int(uiState.selectedSeasonIndex),
            status: uiState.showDetails.status,
            watchedEpisodesCount: uiState.showDetails.watchedEpisodesCount,
            totalEpisodesCount: uiState.showDetails.totalEpisodesCount,
            genreList: uiState.showDetails.genres.map {
                $0.toSwift()
            },
            seasonList: uiState.showDetails.seasonsList.map {
                $0.toSwift()
            },
            providerList: uiState.showDetails.providers.map {
                $0.toSwift()
            },
            trailerList: uiState.showDetails.trailersList.map {
                $0.toSwift()
            },
            castsList: uiState.showDetails.castsList.map {
                $0.toSwift()
            },
            recommendedShowList: uiState.showDetails.recommendedShows.map {
                $0.toSwift()
            },
            similarShows: uiState.showDetails.similarShows.map {
                $0.toSwift()
            },
            continueTrackingEpisodes: uiState.continueTrackingEpisodes.map {
                $0.toSwift()
            },
            continueTrackingScrollIndex: Int(uiState.continueTrackingScrollIndex),
            continueTrackingTitle: String(\.title_continue_tracking),
            dayLabelFormat: { count in String(\.day_label, quantity: count) },
            trackLabel: String(\.following),
            stopTrackingLabel: String(\.unfollow),
            addToListLabel: String(\.btn_add_to_list),
            similarShowsTitle: String(\.title_similar),
            recommendationsTitle: String(\.title_recommended),
            seasonDetailsTitle: String(\.title_season_details),
            showSeasonDetailsHeader: uiState.continueTrackingEpisodes.isEmpty,
            seasonCountFormat: { count in String(\.season_count, quantity: Int(count)) },
            episodesWatchedFormat: { watched, total in String(\.episodes_watched, quantity: Int(total), Int(watched), Int(total)) },
            episodesLeftFormat: { count in String(\.episodes_left, quantity: Int(count), Int(count)) },
            upToDateLabel: String(\.label_up_to_date),
            onAddToCustomList: {
                showCustomList.toggle()
            },
            onAddToLibrary: {
                presenter.dispatch(action: FollowShowClicked(isInLibrary: uiState.showDetails.isInLibrary))
            },
            onSeasonClicked: { index, season in
                let params = ShowSeasonDetailsParam(
                    showTraktId: season.tvShowId,
                    seasonId: season.seasonId,
                    seasonNumber: season.seasonNumber,
                    selectedSeasonIndex: Int32(index)
                )

                presenter.dispatch(action: SeasonClicked(params: params))
            },
            onShowClicked: { id in
                presenter.dispatch(action: DetailShowClicked(id: id))
            },
            onMarkEpisodeWatched: { episode in
                presenter.dispatch(action: MarkEpisodeWatched(
                    showTraktId: episode.showTraktId,
                    episodeId: episode.episodeId,
                    seasonNumber: episode.seasonNumber,
                    episodeNumber: episode.episodeNumber
                ))
            }
        )
    }

    private enum CoordinateSpaces {
        case scrollView
    }
}

private enum DimensionConstants {
    static let imageHeight: CGFloat = 500
    static let collapsedImageHeight: CGFloat = 120.0
}
