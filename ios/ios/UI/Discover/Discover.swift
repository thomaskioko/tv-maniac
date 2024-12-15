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
  
  // MARK: - Discover Content
  @ViewBuilder
  private func discoverLoadedContent(state: DataLoaded) -> some View {
    ScrollView(showsIndicators: false) {
      ZStack(alignment: .bottom) {
        ParallaxHeader(
          coordinateSpace: CoordinateSpaces.scrollView,
          defaultHeight: 550,
          onScroll: { offset in
            let opacity = -offset - 150
            let normalizedOpacity = opacity / 200
            showGlass = max(0, min(1, normalizedOpacity))
          }
        ) {
          headerContent(shows: state.featuredShows)
        }
        showInfoOverlay(state.featuredShows.map { $0.toSwift() })
      }
      discoverListContent(state: state)
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
  
  
  // MARK: - Header Content
  @ViewBuilder
  private func headerContent(shows: [DiscoverShow]) -> some View {
    if shows.isEmpty {
      LoadingIndicatorView()
    } else {
      let items = shows.map{ $0.toSwift() }
      ZStack(alignment: .top) {
        CarouselView(
          items: items,
          currentIndex: $currentIndex,
          onItemScrolled: { item in
            selectedShow = item
          },
          onItemTapped: { id in
            presenter.dispatch(action: ShowClicked(id: id))
          }
        ){ index in
          CarouselItemView(item: items[index])
        }
        
        headerNavigationBar(shows: items)
      }
    }
  }
  
  /// A view that displays a single item in the carousel
  @ViewBuilder
  private func CarouselItemView(item: SwiftShow) -> some View {
    GeometryReader { geometry in
      let scrollViewHeight = geometry.size.height
      
      ZStack(alignment: .bottom) {
        ScrollView(showsIndicators: false) {
          GeometryReader { imageGeometry in
            let minY = imageGeometry.frame(in: .global).minY
            let scrollOffset = minY - geometry.frame(in: .global).minY
            let stretchFactor = max(0, scrollOffset)
            
            PosterItemView(
              title: item.title,
              posterUrl: item.posterUrl,
              posterWidth: geometry.size.width,
              posterHeight: scrollViewHeight + stretchFactor
            )
            .offset(y: -stretchFactor)
          }
          .frame(height: scrollViewHeight)
        }
        .ignoresSafeArea()
        .onTapGesture {
          presenter.dispatch(action: ShowClicked(id: item.tmdbId))
        }
      }
    }
  }
  
  // MARK: - Navigation Bar
  @ViewBuilder
  private func headerNavigationBar(shows: [SwiftShow]) -> some View {
    let show = getShow(currentIndex: currentIndex, shows: shows)
    HStack {
      Text(title)
        .font(.largeTitle)
        .fontWeight(.bold)
      
      Spacer()
      
      if let show = show {
        HStack(spacing: 8) {
          Button(
            action: {
              presenter.dispatch(action: UpdateShowInLibrary(id: show.tmdbId, inLibrary: show.inLibrary))
            }
          ) {
            
            Image(systemName: show.inLibrary == true ? "checkmark" : "plus")
              .font(.avenirNext(size: 17))
              .foregroundColor(.white)
              .frame(width: 28, height: 28)
              .padding(2)
              .background(Color.black.opacity(0.3))
              .clipShape(Circle())
          }
        }
        
        Button(
          action: {
            //TODO:: Invoke account navigation action.
          }) {
            Image(systemName: "person")
              .font(.avenirNext(size: 17))
              .foregroundColor(.white)
              .frame(width: 28, height: 28)
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
  
  @ViewBuilder
  private func showInfoOverlay(_ shows: [SwiftShow]) -> some View {
    VStack(alignment: .leading) {
      Text(selectedShow?.title ?? "")
        .font(.system(size: 46, weight: .bold))
        .foregroundColor(.textColor)
        .lineLimit(1)
        .frame(maxWidth: .infinity, alignment: .center)
      
      if let overview = selectedShow?.overview {
        Text(overview)
          .font(.avenirNext(size: 17))
          .foregroundColor(.white)
          .multilineTextAlignment(.leading)
          .lineLimit(2)
      }
      
      customIndicator(shows)
        .padding(.top, 8)
        .frame(maxWidth: .infinity, alignment: .center)
    }
    .padding(.horizontal)
    .padding(.bottom, 20)
    .frame(maxWidth: .infinity, alignment: .leading)
    .background(
      LinearGradient(
        gradient: Gradient(
          colors: [
            .black,
            .black.opacity(0.8),
            .black.opacity(0.6),
            .black.opacity(0.4),
            .clear
          ]
        ),
        startPoint: .bottom,
        endPoint: .top
      )
      .frame(height: 550)
      .allowsHitTesting(false)
    )
  }
  
  @ViewBuilder
  func customIndicator(_ shows: [SwiftShow]) -> some View {
    HStack(spacing: 5) {
      ForEach(shows.indices, id: \.self) { index in
        Circle()
          .fill(currentIndex - 1 == index ? .white : .gray.opacity(0.5))
          .frame(width: currentIndex - 1 == index ? 10 : 6, height: currentIndex - 1 == index ? 10 : 6)
      }
    }
    .animation(.easeInOut, value: currentIndex)
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
  private func discoverListContent(state: DataLoaded) -> some View {
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
  
  private func getShow(currentIndex: Int, shows: [SwiftShow]) -> SwiftShow? {
    if shows.isEmpty {
      return nil
    }
    let actualIndex = (currentIndex - 1) % shows.count
    return shows[actualIndex >= 0 ? actualIndex : shows.count - 1]
  }
  
  private enum CoordinateSpaces {
    case scrollView
  }
}
