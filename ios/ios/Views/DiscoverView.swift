//
//  DiscoverView.swift
//  tv-maniac
//
//  Created by Thomas Kioko on 19.08.21.
//  Copyright © 2021 orgName. All rights reserved.
//

import SwiftUI
import TvManiac

struct DiscoverView: View {

    private let networkModule: NetworkModule
    private let databaseModule: DatabaseModule
    private let repositoryModule: RepositoryModule
    private let datasourceModule: DataSourceModule


    @ObservedObject var viewModel: DiscoverViewModel

    init(
            networkModule: NetworkModule,
            databaseModule: DatabaseModule
    ) {
        self.networkModule = networkModule
        self.databaseModule = databaseModule
        repositoryModule = RepositoryModule(
                networkModule: self.networkModule,
                databaseModule: self.databaseModule
        )
        datasourceModule = DataSourceModule(repositoryModule: repositoryModule)

        viewModel = DiscoverViewModel(
                networkModule: self.networkModule,
                databaseModule: self.databaseModule,
                interactor: datasourceModule.observeShowsByCategoryInteractor
        )
    }

    var body: some View {


        ZStack {
            Color("Background")
                    .edgesIgnoringSafeArea(.all)

			if viewModel.state.showData.trendingShows.isLoading {
                LoadingIndicatorView()
            } else {
                VStack {
                    ScrollView {

						FeaturedShowsView(shows: viewModel.state.showData.featuredShows.shows)
						
						HorizontalShowsView(showData: viewModel.state.showData.trendingShows)
						
						HorizontalShowsView(showData: viewModel.state.showData.popularShows)

						HorizontalShowsView(showData: viewModel.state.showData.topRatedShows)



                        Spacer()
                    }
                }
            }
        }.onAppear {
                    viewModel.startObservingDiscoverShows()
                }
                .onDisappear {
                    viewModel.stopObservingTrendingShows()
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

            HorizontalShowsGridView(shows: showData.shows)
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
    let shows: [TvShow]


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
    static private var networkModule = NetworkModule()
    static private var databaseModule = DatabaseModule()

    static var previews: some View {
        DiscoverView(networkModule: networkModule, databaseModule: databaseModule)

        DiscoverView(networkModule: networkModule, databaseModule: databaseModule)
                .preferredColorScheme(.dark)
    }
}
