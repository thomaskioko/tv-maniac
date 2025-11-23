import SwiftUI
import SwiftUIComponents
import TvManiacKit

struct ShowDetailsView: View {
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
        .background(Color.background)
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
                            .foregroundColor(.accent)
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
            onAddToCustomList: {
                showCustomList.toggle()
            },
            onAddToLibrary: {
                presenter.dispatch(action: FollowShowClicked(addToLibrary: uiState.showDetails.isInLibrary))
            },
            onSeasonClicked: { index, season in
                let params = ShowSeasonDetailsParam(
                    showId: season.tvShowId,
                    seasonId: season.seasonId,
                    seasonNumber: season.seasonNumber,
                    selectedSeasonIndex: Int32(index)
                )

                presenter.dispatch(action: SeasonClicked(params: params))
            },
            onShowClicked: { id in
                presenter.dispatch(action: DetailShowClicked(id: id))
            }
        )
    }

    private enum CoordinateSpaces {
        case scrollView
    }
}

private enum DimensionConstants {
    static let imageHeight: CGFloat = 550
    static let collapsedImageHeight: CGFloat = 120.0
}
