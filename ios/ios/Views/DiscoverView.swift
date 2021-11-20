//
//  DiscoverView.swift
//  tv-maniac
//
//  Created by Thomas Kioko on 19.08.21.
//  Copyright © 2021 orgName. All rights reserved.
//

import SwiftUI
import shared

struct DiscoverView: View {
	
	private let networkModule: NetworkModule
	private let databaseModule: DatabaseModule
	private let repositoryModule: RepositoryModule
	private let datasourceModule: DataSourceModule
	
	
	@ObservedObject var viewModel: DiscoverViewModel
	
	init(
		networkModule: NetworkModule,
		databaseModule: DatabaseModule
	){
		self.networkModule = networkModule
		self.databaseModule = databaseModule
		self.repositoryModule = RepositoryModule(
			networkModule: self.networkModule,
			databaseModule: self.databaseModule
		)
		self.datasourceModule = DataSourceModule(repositoryModule: self.repositoryModule)
		
		self.viewModel = DiscoverViewModel(
			networkModule: self.networkModule,
			databaseModule: self.databaseModule,
			getTrendingShowsInteractor: datasourceModule.getTrendingShowsInteractor
		)
	}
	
	var body: some View {
		
		
		ZStack {
			Color("Background")
				.edgesIgnoringSafeArea(.all)
			
			VStack {
				ScrollView {
					
					ForEach(viewModel.trendingDataResult, id: \.self) { item in
						
						if item.category == ShowCategory.featured {
							FeaturedShowsView(shows: item.shows)
						} else {
							HorizontalShowsView(title: item.category.title, shows: item.shows)
						}
						
					}
				
					
					Spacer()
				}
				.onAppear{
					viewModel.startObservingTrendingShows()
				}
				.onDisappear{
					viewModel.stopObservingTrendingShows()
				}
				
			}
		}
	}
	
}

struct FeaturedShowsView: View {
	
	
	@SwiftUI.State var numberOfPages: Int = 0
	@SwiftUI.State var selectedIndex = 0
	
	let shows: [TvShow]
	
	
	var body: some View {
		
		VStack {
			
			if shows.count != 0 {
				TabView {
					
					ForEach(shows, id:\.self) { item in
						let url = URL(string: item.posterImageUrl)!
						
						AsyncImage(
							url: url,
							placeholder: { Text("Loading ...") },
							image: { Image(uiImage: $0).resizable() }
						)
							.frame(height: 450) // 2:3 aspect ratio
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
		.frame(height: 550)
		.padding(.bottom, 20)
	}
}

struct HorizontalShowsView: View {
	
	let title: String
	let shows: [TvShow]
	
	var body: some View{
		VStack {
			
			LabelView(title: title)
			
			HorizontalShowsGridView(shows: shows)
		}
	}
}

struct LabelView: View {
	
	let title: String

	var body: some View{
		HStack {
			
			LabelTitleText(text: title)
			
			Spacer()
			
			Button(action: {}){
				LabelText(text: "More")
			}
		}
		.padding(.leading)
	}
}

struct HorizontalShowsGridView: View {
	let shows: [TvShow]
	
	
	var body: some View {
		ScrollView(.horizontal) {
			LazyHStack {
				ForEach(shows, id:\.self) { item in
					
					let url = URL(string: item.posterImageUrl)!
					
					AsyncImage(
						url: url,
						placeholder: { Text("Loading ...") },
						image: { Image(uiImage: $0).resizable() }
					)
						.frame(width: 160, height: 160)
						.clipShape(RoundedRectangle(cornerRadius: 5))
				}
			}
		}
		.padding(.leading)
	}
}


struct DiscoverView_Previews: PreviewProvider {
	static private var networkModule = NetworkModule()
	static private var databaseModule = DatabaseModule()
	
	static var previews: some View {
		DiscoverView(networkModule: networkModule, databaseModule: databaseModule)
		
		DiscoverView(networkModule: networkModule, databaseModule: databaseModule)
			.preferredColorScheme(.dark)
	}
}
