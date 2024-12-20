import SwiftUI
import SwiftUIComponents
import TvManiac
import TvManiacKit
import TvManiacUI

struct ShowDetailsView: View {
  private let presenter: ShowDetailsPresenter
  @StateObject @KotlinStateFlow private var uiState: ShowDetailsContent
  @State private var showGlass: Double = 0

  init(presenter: ShowDetailsPresenter) {
    self.presenter = presenter
    _uiState = .init(presenter.state)
  }

  var body: some View {
    if !uiState.isUpdating && uiState.showDetails == nil {
      FullScreenView(
        buttonText: "Retry",
        action: { presenter.dispatch(action: ReloadShowDetails()) }
      )
    } else if let showDetails = uiState.showDetails {
      ParallaxView(
        imageHeight: DimensionConstants.imageHeight,
        collapsedImageHeight: DimensionConstants.collapsedImageHeight,
        header: { proxy in
          HeaderView(
            title: showDetails.title,
            overview: showDetails.overview,
            backdropImageUrl: showDetails.backdropImageUrl,
            status: showDetails.status,
            year: showDetails.year,
            language: showDetails.language,
            rating: showDetails.rating,
            progress: proxy.getTitleOpacity(
              geometry: proxy,
              imageHeight: DimensionConstants.imageHeight,
              collapsedImageHeight: DimensionConstants.collapsedImageHeight
            ),
            headerHeight: proxy.getHeightForHeaderImage(proxy)
          )
        },
        content: {
          showInfoDetails(show: showDetails)
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
      .overlay(
        GlassToolbar(title: uiState.showDetails?.title ?? "", opacity: showGlass),
        alignment: .top
      )
      .animation(.easeInOut(duration: 0.25), value: showGlass)
      .coordinateSpace(name: CoordinateSpaces.scrollView)
      .edgesIgnoringSafeArea(.top)
    }
  }

  @ViewBuilder
  func showInfoDetails(show: ShowDetails) -> some View {
    switch onEnum(of: uiState.showInfo) {
    case .loading, .empty:
      LoadingIndicatorView(animate: true)
    case .error:
      FullScreenView(buttonText: "Retry", action: { presenter.dispatch(action: ReloadShowDetails()) })
    case let .loaded(state):
      ShowInfoView(
        isFollowed: show.isFollowed,
        openTrailersInYoutube: state.openTrailersInYoutube,
        genreList: show.genres.map { $0.toSwift() },
        seasonList: state.seasonsList.map { $0.toSwift() },
        providerList: state.providers.map { $0.toSwift() },
        trailerList: state.trailersList.map { $0.toSwift() },
        castsList: state.castsList.map { $0.toSwift() },
        recommendedShowList: state.recommendedShowList.map { $0.toSwift() },
        similarShows: state.similarShows.map { $0.toSwift() },
        onWatchTrailer: {
          presenter.dispatch(action: WatchTrailerClicked(id: show.tmdbId))
        },
        onAddToLibrary: {
          presenter.dispatch(action: FollowShowClicked(addToLibrary: show.isFollowed))
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
  }

  private enum CoordinateSpaces {
    case scrollView
  }
}

private enum DimensionConstants {
  static let imageHeight: CGFloat = 550
  static let collapsedImageHeight: CGFloat = 120.0
}
