import SwiftUI
import TvManiac
import TvManiacUI
import TvManiacKit
import SwiftUIComponents

struct Discover: View {
  private let presenter: DiscoverShowsPresenter
  @StateObject @KotlinStateFlow private var uiState: DiscoverState
  @State private var currentIndex = 0
  @State private var showNavigationBar = false
  @State private var selectedShow: SwiftShow?
  @State private var showGlass: Double = 0
  private let title = "Discover"

  init(presenter: DiscoverShowsPresenter) {
    self.presenter = presenter
    _uiState = .init(presenter.state)
  }

  var body: some View {
    switch onEnum(of: uiState) {
      case .loading:
        LoadingIndicatorView()
      case .dataLoaded(let state):
        discoverLoadedContent(state: state)
      case .emptyState:
        emptyView
      case .errorState(let error):
        FullScreenView(
          systemName: "exclamationmark.arrow.triangle.2.circlepath",
          message: error.errorMessage ?? "Something went wrong!!"
        )
    }
  }

  // MARK: - Header Content
  @ViewBuilder
  private func headerContent(state: DataLoaded, currentIndex: Binding<Int>) -> some View {
    ZStack(alignment: .top) {
      CarouselView(
        items: state.featuredShows.map{ $0.toSwift() },
        currentIndex: currentIndex,
        onItemScrolled: { item in
          selectedShow = item
        },
        onItemTapped: { id in
          presenter.dispatch(action: ShowClicked(id: id))
        }
      )

      headerNavigationBar()
    }
  }

  // MARK: - Navigation Bar
  @ViewBuilder
  private func headerNavigationBar() -> some View {
    HStack {
      Text(title)
        .font(.largeTitle)
        .fontWeight(.bold)

      Spacer()

      HStack(spacing: 8) {
        Button(
          action: {
            if let show = selectedShow {
              presenter.dispatch(action: UpdateShowInLibrary(id: show.tmdbId, inLibrary: show.inLibrary))
            }
          }
        ) {

          Image(systemName: selectedShow?.inLibrary == true ? "checkmark" : "plus")
            .font(.title2)
            .foregroundColor(.white)
            .frame(width: 32, height: 32)
            .padding(2)
            .background(Color.black.opacity(0.3))
            .clipShape(Circle())
        }

        Button(
          action: {
            //TODO:: Invoke account navigation action.
          }) {
            Image(systemName: "person")
              .font(.title2)
              .foregroundColor(.white)
              .frame(width: 32, height: 32)
              .padding(2)
              .background(Color.black.opacity(0.3))
              .clipShape(Circle())
          }
      }
    }
    .padding(.horizontal)
    .padding(.top, 70)
    .padding(.bottom, 8)
    .foregroundColor(.white)
  }

  // MARK: - Discover Content
  @ViewBuilder
  private func discoverLoadedContent(state: DataLoaded) -> some View {
    ScrollView(showsIndicators: false) {
      ParallaxHeader(
        coordinateSpace: CoordinateSpaces.scrollView,
        defaultHeight: 500,
        onScroll: { offset in
          let opacity = -offset - 150
          let normalizedOpacity = opacity / 200
          showGlass = max(0, min(1, normalizedOpacity))
        }
      ) {
        headerContent(
          state: state,
          currentIndex: $currentIndex
        )
      }

      discoverListContent(
        state: state,
        presenter: presenter
      )
    }
    .background(Color.background)
    .navigationBarTitleDisplayMode(.inline)
    .navigationBarColor(backgroundColor: .clear)
    .overlay(
      GlassToolbar(title: title, opacity: showGlass),
      alignment: .top
    )
    .animation(.easeInOut(duration: 0.25), value: showGlass)
    .coordinateSpace(name: CoordinateSpaces.scrollView)
    .edgesIgnoringSafeArea(.top)
  }

  // MARK: - Discover Content
  @ViewBuilder
  private func discoverContent(dataLoaded: DataLoaded) -> some View {
    VStack {
      HorizontalItemListView(
        title: "Upcoming",
        chevronStyle: .chevronOnly,
        items: dataLoaded.upcomingShows.map { $0.toSwift() },
        onClick: { id in presenter.dispatch(action: ShowClicked(id: id)) },
        onMoreClicked: { presenter.dispatch(action: UpComingClicked()) }
      )

      HorizontalItemListView(
        title: "Trending Today",
        chevronStyle: .chevronOnly,
        items: dataLoaded.trendingToday.map { $0.toSwift() },
        onClick: { id in presenter.dispatch(action: ShowClicked(id: id)) },
        onMoreClicked: { presenter.dispatch(action: TrendingClicked()) }
      )

      HorizontalItemListView(
        title: "Popular",
        chevronStyle: .chevronOnly,
        items: dataLoaded.popularShows.map { $0.toSwift() },
        onClick: { id in presenter.dispatch(action: ShowClicked(id: id)) },
        onMoreClicked: { presenter.dispatch(action: PopularClicked()) }
      )

      HorizontalItemListView(
        title: "Top Rated",
        chevronStyle: .chevronOnly,
        items: dataLoaded.topRatedShows.map { $0.toSwift() },
        onClick: { id in presenter.dispatch(action: ShowClicked(id: id)) },
        onMoreClicked: { presenter.dispatch(action: TopRatedClicked()) }
      )
    }
    .padding(.top, 16)
    .background(Color.background)
    .offset(y: -10)
  }

  // MARK: - Discover List Content
  @ViewBuilder
  private func discoverListContent(state: DataLoaded, presenter: DiscoverShowsPresenter) -> some View {
    VStack {
      HorizontalItemListView(
        title: "Upcoming",
        chevronStyle: .chevronOnly,
        items: state.upcomingShows.map { $0.toSwift() },
        onClick: { id in presenter.dispatch(action: ShowClicked(id: id)) },
        onMoreClicked: { presenter.dispatch(action: UpComingClicked()) }
      )

      HorizontalItemListView(
        title: "Trending Today",
        chevronStyle: .chevronOnly,
        items: state.trendingToday.map { $0.toSwift() },
        onClick: { id in presenter.dispatch(action: ShowClicked(id: id)) },
        onMoreClicked: { presenter.dispatch(action: TrendingClicked()) }
      )

      HorizontalItemListView(
        title: "Popular",
        chevronStyle: .chevronOnly,
        items: state.popularShows.map { $0.toSwift() },
        onClick: { id in presenter.dispatch(action: ShowClicked(id: id)) },
        onMoreClicked: { presenter.dispatch(action: PopularClicked()) }
      )

      HorizontalItemListView(
        title: "Top Rated",
        chevronStyle: .chevronOnly,
        items: state.topRatedShows.map { $0.toSwift() },
        onClick: { id in presenter.dispatch(action: ShowClicked(id: id)) },
        onMoreClicked: { presenter.dispatch(action: TopRatedClicked()) }
      )
    }
    .padding(.top, 16)
    .background(Color.background)
    .offset(y: -10)
  }

  // MARK: - Empty View
  @ViewBuilder
  private var emptyView: some View {
    VStack {
      Image(systemName: "list.bullet.below.rectangle")
        .resizable()
        .aspectRatio(contentMode: .fit)
        .foregroundColor(Color.accent)
        .font(Font.title.weight(.thin))
        .frame(width: 160, height: 180)

      Text("Looks like your stash is empty")
        .titleSemiBoldFont(size: 18)
        .padding(.top, 8)

      Text("Could be that you forgot to add your TMDB API Key. Once you set that up, you can get lost in the vast world of Tmdb's collection.")
        .captionFont(size: 16)
        .padding(.top, 1)
        .padding(.bottom, 16)

      Button(action: {
        presenter.dispatch(action: ReloadData())
      }, label: {
        Text("Retry")
          .bodyMediumFont(size: 16)
          .foregroundColor(Color.accent)
      })
      .buttonStyle(BorderlessButtonStyle())
      .padding(16)
      .background(
        RoundedRectangle(cornerRadius: 5)
          .stroke(Color.accent, lineWidth: 2)
          .background(.clear)
          .cornerRadius(2))
    }
    .frame(maxWidth: .infinity, maxHeight: .infinity)
    .padding([.trailing, .leading], 16)
  }

  private enum CoordinateSpaces {
    case scrollView
  }
}
