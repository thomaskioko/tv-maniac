//
//  ShowInfoView.swift
//  tv-maniac
//
//  Created by Thomas Kioko on 7/27/24.
//  Copyright © 2024 orgName. All rights reserved.
//

import SwiftUI
import TvManiac

struct ShowInfoView: View {
    let loadedState: ShowInfoState
    let show: ShowDetails
    let component: ShowDetailsComponent
    @Binding var titleRect: CGRect
    
    var body: some View {
        VStack {
            switch onEnum(of: loadedState) {
                case .loading:
                    LoadingIndicatorView(animate: true)
                case .empty, .error:
                    ErrorUiView(
                        systemImage: "exclamationmark.triangle.fill",
                        action: { component.dispatch(action: ReloadShowDetails()) }
                    )
                case .loaded(let state):
                    LoadedContent(
                        loadedState: state,
                        show: show
                    )
            }
        }
    }
    
    @ViewBuilder
    private func LoadedContent(
        loadedState: ShowInfoStateLoaded,
        show: ShowDetails
    ) -> some View {
        VStack {
            
            Spacer(minLength: nil)
                .background(GeometryGetter(rect: self.$titleRect))
            
            if(!show.genres.isEmpty) {
                ScrollView(.horizontal, showsIndicators: false) {
                    HStack(alignment: .center) {
                        ForEach(show.genres, id: \.self) { label in
                            ChipView(label: label)
                        }
                    }
                    .padding(.horizontal)
                }
            }
            
            HStack(alignment: .center, spacing: 8) {
                BorderedButton(
                    text: "Watch Trailer",
                    systemImageName: "film.fill",
                    isOn: false,
                    action: {
                        component.dispatch(action: WatchTrailerClicked(id: show.tmdbId))}
                )
                
                let followText = show.isFollowed ? "Unfollow Show" : "Follow Show"
                let buttonSystemImage = show.isFollowed ? "checkmark.square.fill" : "plus.square.fill.on.square.fill"
                
                BorderedButton(
                    text: followText,
                    systemImageName: buttonSystemImage,
                    isOn: false,
                    action: { component.dispatch(action: FollowShowClicked(addToLibrary: show.isFollowed)) }
                )
            }
            .padding(.top, 10)
            
            SeasonsRowView(
                seasonsList: loadedState.seasonsList,
                onClick: { params in  component.dispatch(action: SeasonClicked(params: params)) }
            )
            
            ProvidersList(items: loadedState.providers)
            
            TrailerListView(trailers: loadedState.trailersList, openInYouTube: loadedState.openTrailersInYoutube)
            
            CastListView(casts: loadedState.castsList)
            
            HorizontalShowsListView(
                title: "Recommendations",
                items: loadedState.recommendedShowList,
                onClick: { id in component.dispatch(action: DetailShowClicked(id: id)) }
            )
            
            HorizontalShowsListView(
                title: "Similar Shows",
                items: loadedState.similarShows,
                onClick: { id in component.dispatch(action: DetailShowClicked(id: id)) }
            )
        }
    }
}
