//
//  ShowDetailView.swift
//  tv-maniac
//
//  Created by Thomas Kioko on 13.01.22.
//  Copyright © 2022 orgName. All rights reserved.
//

import SwiftUI
import TvManiac
import ScalingHeaderScrollView

struct ShowDetailView: View {
    
    private let maxHeight : CGFloat = 520
    private let minHeight = 110.0
    private let presenter: ShowDetailsPresenter
    
    @StateValue private var uiState: ShowDetailsState
    @State var progress: CGFloat = 0
    
    init(presenter: ShowDetailsPresenter){
        self.presenter = presenter
        _uiState = StateValue(presenter.state)
    }
    
    var body: some View {
        ZStack {
            ScalingHeaderScrollView {
                HeaderContentView(
                    show: uiState.showDetails,
                    progress: progress,
                    maxHeight: maxHeight,
                    onAddToLibraryClick: { add in
                        presenter.dispatch(action: FollowShowClicked(addToLibrary: add))
                    },
                    onWatchTrailerClick: { id in
                        presenter.dispatch(action: WatchTrailerClicked(id: id))
                    }
                )
                
            } content: {
                seasonContent
                
                ProvidersList(items: uiState.providers)
                
                TrailerListView(trailers: uiState.trailersList, openInYouTube: uiState.openTrailersInYoutube)
                
                CastListView(casts: uiState.castsList)
                
                HorizontalShowsListView(
                    title: "Recommendations",
                    items: uiState.recommendedShowList,
                    onClick: { id in presenter.dispatch(action: DetailShowClicked(id: id)) }
                )
                
                HorizontalShowsListView(
                    title: "Similar Shows",
                    items: uiState.similarShows,
                    onClick: { id in presenter.dispatch(action: DetailShowClicked(id: id)) }
                )
                
            }
            .height(min: minHeight, max: maxHeight)
            .collapseProgress($progress)
            .allowsHeaderGrowth()
            .hideScrollIndicators()
            .background(Color.background)
            .shadow(radius: progress)
            
            topBar
        }
        .ignoresSafeArea()
    }
    
    private var topBar: some View {
        VStack {
            HStack {
                Button("", action: { presenter.dispatch(action: DetailBackClicked()) })
                    .buttonStyle(CircleButtonStyle(imageName: "arrow.backward"))
                    .padding(.leading, 17)
                    .padding(.top, 60)
                Spacer()
            }
            Spacer()
        }
        .ignoresSafeArea()
    }
    
    private var seasonContent: some View {
        VStack {
            if !uiState.seasonsList.isEmpty {
                let seasons = uiState.seasonsList
                TitleView(
                    title: "Browse Seasons",
                    showChevron: false
                )
                
                ScrollView(.horizontal, showsIndicators: false) {
                    LazyHStack {
                        ForEach(seasons, id: \.self) { season in
                            
                            Button(action: {}){
                                ChipView(label: season.name)
                            }
                            
                        }
                    }
                    .padding(.trailing, 16)
                    .padding(.leading, 16)
                }
            }
        }
    }

    private var recommendedShows: some View {
        VStack {
            TitleView(title: "Recommendations", showChevron: true)
            
        }
    }
}


