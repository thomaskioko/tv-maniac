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
        discoverContent(state: state)
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
  private func discoverContent(state: DataLoaded) -> some View {
    ScrollView(showsIndicators: false) {
      ParallaxHeader(
        coordinateSpace: CoordinateSpaces.scrollView,
        defaultHeight: 500,
        onScroll: { offset in
          if offset < -350 {
            showNavigationBar = true
          } else if offset > -150 {
            showNavigationBar = false
          }
        }
      ) {
        ZStack(alignment: .top) {
          CarouselView(
            items: state.featuredShows.map{ $0.toSwift() },
            currentIndex: $currentIndex,
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
      
      //Content placeholder
      contentPlaceholder()
    }
    .navigationBarTitleDisplayMode(.inline)
    .toolbar {
      ToolbarItem(placement: .principal) {
        Text(title)
          .font(.system(size: 18, weight: .bold))
          .foregroundColor(.white)
          .opacity(showNavigationBar ? 1 : 0)
          .animation(.easeInOut(duration: 0.25), value: showNavigationBar)
      }
    }
    .navigationBarHidden(!showNavigationBar)
    .animation(
      .spring(
        response: 0.35,
        dampingFraction: 0.8,
        blendDuration: 0.25
      ),
      value: showNavigationBar
    )
    .coordinateSpace(name: CoordinateSpaces.scrollView)
    .background(Color.background)
    .edgesIgnoringSafeArea(.top)
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
  
  // MARK: - Content Placeholder
  @ViewBuilder
  private func contentPlaceholder() -> some View {
    Rectangle()
      .fill(.blue)
      .frame(height: 1000)
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
}


private enum CoordinateSpaces {
  case scrollView
}
