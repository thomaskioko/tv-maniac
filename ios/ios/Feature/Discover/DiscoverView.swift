import SwiftUI
import TvManiac

struct DiscoverView: View {
	
	
	@ObservedObject var observable = ObservableViewModel<DiscoverShowsViewModel, DiscoverShowsState>(
		viewModel: DiscoverShowsViewModel()
	)
	
	
	var body: some View {
		
		ZStack {
			Color("Background")
				.edgesIgnoringSafeArea(.all)
			
			if observable.state is DiscoverShowsState.InProgress {
				LoadingIndicatorView()
			} else if observable.state is DiscoverShowsState.Error {
				//TODO:: Show Error
				EmptyView()
			} else if observable.state is DiscoverShowsState.Success {
				
				VStack {
					ScrollView {
						
						if let result = observable.state as? DiscoverShowsState.Success{
							
							FeaturedShowsView(shows: result.data.featuredShows.showUiModels)
							
							HorizontalShowsView(showData: result.data.trendingShows)
							
							HorizontalShowsView(showData: result.data.popularShows)
							
							HorizontalShowsView(showData: result.data.topRatedShows)
							
						}
						
						Spacer()
					}
				}
			}
		}.onAppear {
			observable.viewModel.attach()
		}
		.onDisappear {
			
			observable.viewModel.detach()
		}
	}
	
}

struct FeaturedShowsView: View {
	
	
	@SwiftUI.State var numberOfPages: Int = 0
	@SwiftUI.State var selectedIndex = 0
	
	let shows: [ShowUiModel]
	
	
	var body: some View {
		
		VStack {
			
			if shows.count != 0 {
				TabView {
					
					ForEach(shows, id: \.self) { item in
						let url = URL(string: item.posterImageUrl)!
						
						AsyncImage(
							url: url,
							placeholder: { Text("Loading ...") },
							image: { Image(uiImage: $0).resizable() }
						)
							.frame(height: 500)
							.clipShape(RoundedRectangle(cornerRadius: 10))
							.padding()
					}
				}
				.indexViewStyle(PageIndexViewStyle(backgroundDisplayMode: .always))
				.tabViewStyle(PageTabViewStyle(indexDisplayMode: .always))
				.onAppear {
					UIPageControl.appearance().currentPageIndicatorTintColor = .white
					UIPageControl.appearance().pageIndicatorTintColor = UIColor.black.withAlphaComponent(0.2)
				}
			}
		}
		.frame(height: 600)
		.padding(.bottom, 20)
	}
}

struct HorizontalShowsView: View {
	
	let showData: DiscoverShowResult.DiscoverShowsData
	
	var body: some View {
		VStack {
			
			LabelView(title: showData.category.title)
			
			HorizontalShowsGridView(shows: showData.showUiModels)
		}
	}
}

struct LabelView: View {
	
	let title: String
	
	var body: some View {
		HStack {
			
			LabelTitleText(text: title)
			
			Spacer()
			
			Button(action: {}) {
				LabelText(text: "More")
			}
		}
		.padding(.leading)
	}
}

struct HorizontalShowsGridView: View {
	let shows: [ShowUiModel]
	
	
	var body: some View {
		ScrollView(.horizontal) {
			LazyHStack {
				ForEach(shows, id: \.self) { item in
					
					let url = URL(string: item.posterImageUrl)!
					
					AsyncImage(
						url: url,
						placeholder: { Text("Loading ...") },
						image: { Image(uiImage: $0).resizable() }
					)
						.frame(width: 140, height: 180)
						.clipShape(RoundedRectangle(cornerRadius: 5))
				}
			}
		}
		.padding(.leading)
	}
}


struct DiscoverView_Previews: PreviewProvider {
	
	static var previews: some View {
		DiscoverView()
		
		DiscoverView()
			.preferredColorScheme(.dark)
	}
}
