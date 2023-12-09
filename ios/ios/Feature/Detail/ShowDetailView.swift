//
//  ShowDetailView.swift
//  tv-maniac
//
//  Created by Thomas Kioko on 13.01.22.
//  Copyright © 2022 orgName. All rights reserved.
//

import SwiftUI
import TvManiac

struct ShowDetailView: View {
    
    private let presenter: ShowDetailsPresenter
    
    @StateValue
    private var uiState: ShowDetailsState
    
    // var animationID: Namespace.ID
    @State var offset: CGFloat = 0
    @State var titleOffset: CGFloat = 0
    @State var size: CGSize = CGSize(width: UIScreen.main.bounds.width, height: UIScreen.main.bounds.height)
    
    let maxHeight = CGFloat(520)
    
    init(presenter: ShowDetailsPresenter){
        self.presenter = presenter
        _uiState = StateValue(presenter.state)
    }
    
    var body: some View {
        
        VStack {
            ScrollView(.vertical, showsIndicators: false) {
                VStack {
                    
                    ArtWork(show: uiState.show, presenter: presenter)
                    
                    ShowBodyView(
                        seasonList: uiState.seasonsContent.seasonsList,
                        trailerList: uiState.trailersContent.trailersList,
                        similarShowsList: uiState.similarShowsContent.similarShows,
                        onClick: { id in presenter.dispatch(action: DetailShowClicked(id: id))}
                    )
                }
            }
            .coordinateSpace(name: "SCROLL")
        }
        .overlay(alignment: .top){
            TopNavBarView(showTitle: uiState.show.title)
        }
        .background(Color.background)
        .navigationBarHidden(true)
        .ignoresSafeArea()
    }
    
    @ViewBuilder
    func ArtWork(show: Show, presenter: ShowDetailsPresenter) -> some View {
        let height = size.height * 0.45
        
        GeometryReader { proxy in
            let size = proxy.size
            let minY = proxy.frame(in: .named("SCROLL")).minY
            let progress = minY / (height * (minY > 0 ? 0.5 : 0.8))
            
            ShowPosterImage(
                posterSize: .max,
                imageUrl: show.backdropImageUrl,
                showTitle: show.title,
                showId: show.traktId,
                onClick: {  presenter.dispatch(action: DetailShowClicked(id: show.traktId))}
            )
            .aspectRatio(contentMode: .fill)
            .frame(width: size.width, height: size.height + (minY > 0 ? minY : 0))
            .foregroundStyle(.ultraThinMaterial)
            .clipped()
            .overlay( content : {
                ZStack(alignment: .bottom) {
                    
                    Rectangle()
                        .fill(
                            .linearGradient(colors: [
                                Color.background,
                                .clear,
                                Color.background.opacity(0 - progress),
                                Color.background.opacity(0.1 - progress),
                                Color.background.opacity(0.3 - progress),
                                Color.background.opacity(0.5 - progress),
                                Color.background.opacity(0.8 - progress),
                                Color.background.opacity(1),
                            ], startPoint: .top, endPoint: .bottom)
                        )
                    
                    //Header Content
                    HeaderContentView(show: show, presenter: presenter)
                        .opacity(1 + (progress > 0 ? -progress : progress))
                        .padding(.horizontal,16)
                    // Moving With ScrollView
                        .offset(y: minY < 0 ? minY : 0)
                }
            })
            .offset(y: -minY)
        }
        .frame(height: maxHeight)
    }
    
    
    @ViewBuilder
    func TopNavBarView(showTitle: String)->some View{
        GeometryReader{proxy in
            let minY = proxy.frame(in: .named("SCROLL")).minY
            let height = maxHeight * 0.45
            let progress = minY / (height * (minY > 0 ? 0.5 : 0.8))
            let titleProgress = minY / height
            
            TopNavBar(
                titleProgress: titleProgress,
                title: showTitle,
                action: { presenter.dispatch(action: DetailBackClicked())}
            )
            .padding(.top, 45)
            .padding([.horizontal,],15)
            .background(content: {
                Color.background
                    .opacity(-progress > 1 ? 1 : 0)
            })
            .offset(y: -minY)
        }
    }
    
    @ViewBuilder
    func HeaderContentView(show: Show, presenter: ShowDetailsPresenter) -> some View {
        
        VStack(spacing: 0){
            Text(show.title)
                .titleFont(size: 30)
                .foregroundColor(Color.text_color_bg)
                .lineLimit(1)
                .padding(.top, 8)
            
            Text(show.overview)
                .bodyFont(size: 18)
                .foregroundColor(Color.text_color_bg)
                .lineLimit(3)
                .padding(.top, 1)
            
            ShowInfoRow(show: show)
                .padding(.top, 5)
            
            GenresRowView(genres: show.genres)
                .padding(.top, 5)
            
            HStack(alignment: .center, spacing: 8) {
                
                BorderedButton(
                    text: "Watch Trailer",
                    systemImageName: "film.fill",
                    color: .accent,
                    borderColor: .grey_200,
                    isOn: false,
                    action: { presenter.dispatch(action: WatchTrailerClicked(id: show.traktId)) }
                )
                
                let followText = if (!show.isFollowed) { "Follow Show" } else { "Unfollow Show"}
                let buttonSystemImage = if (!show.isFollowed) { "plus.square.fill.on.square.fill" } else { "checkmark.square.fill"}
                
                BorderedButton(
                    text: followText,
                    systemImageName: buttonSystemImage,
                    color: .accent,
                    borderColor: .grey_200,
                    isOn: false,
                    action: { presenter.dispatch(action: FollowShowClicked(addToLibrary: show.isFollowed)) }
                )
            }
            .padding(.bottom, 16)
            .padding(.top, 10)
        }
        
    }
}
