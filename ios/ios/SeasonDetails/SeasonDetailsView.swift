//
//  SeasonDetailsView.swift
//  tv-maniac
//
//  Created by Thomas Kioko on 21.12.23.
//  Copyright © 2023 orgName. All rights reserved.
//

import SwiftUI
import ScalingHeaderScrollView
import TvManiac

struct SeasonDetailsView: View {
    
    private let presenter: SeasonDetailsPresenter
    
    @StateValue private var uiState: SeasonDetailsState
    
    @State private var progress: CGFloat = 0
    @State private var isTruncated = false
    @State private var showFullText = false
    @State private var showingAlert: Bool = false
    @State private var showModal =  false

    
    init(presenter: SeasonDetailsPresenter) {
        self.presenter = presenter
        _uiState = StateValue(presenter.state)
    }
    
    var body: some View {
    
        ZStack {
            ScalingHeaderScrollView {
                headerContent
            } content: {
                VStack(alignment: .leading, spacing: 0) {
                    seasonOverview
                    episodeContent
                    CastListView(casts: toCastsList(uiState.seasonCast))
                }
            }
            .height(min: DimensionConstants.minHeight, max: DimensionConstants.imageHeight)
            .collapseProgress($progress)
            .allowsHeaderGrowth()
            .hideScrollIndicators()
            .shadow(radius: progress)
            .onAppear { showModal = uiState.showSeasonWatchStateDialog }
            
            TopBar(onBackClicked: { presenter.dispatch(action: BackClicked_()) })
        }
        .ignoresSafeArea()
        .background(Color.background)
        .sheet(isPresented: $showModal) {
            ImageGalleryContentView()
        }
    }

    
    private var headerContent: some View {
        ZStack {
            HeaderCoverArtWorkView(
                backdropImageUrl: uiState.imageUrl,
                posterHeight: DimensionConstants.imageHeight
            )
            .frame(height: DimensionConstants.imageHeight)
            
            ZStack(alignment: .bottom) {
                
                Rectangle()
                    .fill(
                        .linearGradient(colors: [
                            .clear,
                            .clear,
                            .clear,
                            Color.background.opacity(0.6),
                            Color.background.opacity(0.8),
                            Color.background,
                        ], startPoint: .top, endPoint: .bottom)
                    )
                
                VStack {
                    HStack(spacing: 16) {
                        Image(systemName: "photo.fill.on.rectangle.fill")
                            .resizable()
                            .frame(width: 28.0, height: 28.0)
                            .fontDesign(.rounded)
                            .font(.callout)
                            .fontWeight(.regular)
                            .foregroundColor(.secondary)
                            .alignmentGuide(.view) { d in d[HorizontalAlignment.leading] }
                        
                        
                        Text("^[\(uiState.seasonImages.count) Image](inflect: true)")
                            .bodyMediumFont(size: 16)
                            .foregroundColor(.text_color_bg)
                            .lineLimit(1)
                            .alignmentGuide(.view) { d in d[HorizontalAlignment.center] }
                        
                        Spacer()
                    }
                    .padding(16)
                    .opacity(1 + (progress > 0 ? -progress : progress))
                    .contentShape(Rectangle())
                    .onTapGesture {
                        presenter.dispatch(action: SeasonGalleryClicked())
                        showModal.toggle()
                    }
                    
                    ProgressView(value: uiState.watchProgress, total: 1)
                        .progressViewStyle(RoundedRectProgressViewStyle())
                }
                
                Text(uiState.seasonName)
                    .bodyFont(size: 24)
                    .fontWeight(.semibold)
                    .lineLimit(1)
                    .padding(.leading, 75.0)
                    .opacity(progress)
                    .opacity(max(0, min(1, (progress - 0.75) * 4.0)))
                    .frame(maxWidth: .infinity, alignment: .leading)
                    .padding(.bottom, 30)
            }
        }
    }
    
    private var seasonOverview: some View {
        VStack(alignment: .leading) {
            Text("Overview")
                .bodyFont(size: 26)
                .fontWeight(.semibold)
                .frame(maxWidth: .infinity, alignment: .leading)
                .padding(.bottom, 8)
                .padding(.bottom, 0.5)
            
            Text(uiState.seasonOverview)
                .font(.callout)
                .padding([.top], 2)
                .lineLimit(showFullText ? nil : 4)
                .multilineTextAlignment(.leading)
                .background(
                    Text(uiState.seasonOverview)
                        .lineLimit(4)
                        .font(.callout)
                        .padding([.top], 2)
                        .background(GeometryReader { displayedGeometry in
                            ZStack {
                                Text(uiState.seasonOverview)
                                    .font(.callout)
                                    .padding([.top], 2)
                                    .background(GeometryReader { fullGeometry in
                                        // And compare the two
                                        Color.clear.onAppear {
                                            self.isTruncated = fullGeometry.size.height > displayedGeometry.size.height
                                        }
                                    })
                            }
                            .frame(height: .greatestFiniteMagnitude)
                        })
                        .hidden() // Hide the background
                )
            
            if isTruncated {
                Text(showFullText ? "Collapse" : "Show More")
                    .fontDesign(.rounded)
                    .textCase(.uppercase)
                    .font(.caption)
                    .foregroundStyle(Color.accent)
                    .padding(.top, 4)
                
            }
        }
        .onTapGesture {
            withAnimation { showFullText.toggle() }
        }
        .padding(16)
    }
    
    private var episodeContent: some View {
        Collapsible(
            episodeCount: uiState.episodeCount,
            watchProgress: CGFloat(uiState.watchProgress),
            isCollapsed: uiState.expandEpisodeItems,
            onCollapseClicked: { presenter.dispatch(action: OnEpisodeHeaderClicked()) },
            onWatchedStateClicked: {
                presenter.dispatch(action: UpdateSeasonWatchedState())
                showingAlert = !uiState.showSeasonWatchStateDialog
            }
        ) {
            VStack {
                VerticalEpisodeListView(items: uiState.episodeDetailsList)
            }
        }
        .alert(isPresented: $showingAlert, content: {
            let title = if(uiState.isSeasonWatched){
                "Mark as unwatched"
            } else {
                "Mark as watched"
            }
            let messageBody = if(uiState.isSeasonWatched){
                "Are you sure you want to mark the entire season as unwatched?"
            } else {
                "Are you sure you want to mark the entire season as watched?"
            }
            
           return Alert(
                title: Text(title),
                message: Text(messageBody),
                primaryButton: .default(Text("No")) {
                    
                    
                },
                secondaryButton: .default(Text("Yes"))
            )
        })
        
    }
    
    private func toCastsList(_ list: [Cast]) -> [Casts] {
        return list.map{ (cast) -> Casts in
            Casts(id: cast.id, name: cast.name, profileUrl: cast.profileUrl, characterName: cast.characterName)
        }
    }
}


private struct DimensionConstants {
    static let imageHeight: CGFloat = 320
    static let minHeight: CGFloat = 120.0
}
