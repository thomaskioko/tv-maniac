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
    
    private let component: SeasonDetailsComponent

    @Environment(\.presentationMode) var presentationMode
    
    @StateFlow private var uiState: SeasonDetailState
    @State private var progress: CGFloat = 0
    @State private var isTruncated = false
    @State private var showFullText = false
    @State private var showModal =  false
    
    init(component: SeasonDetailsComponent) {
        self.component = component
        _uiState = StateFlow(component.state)
    }
    
    var body: some View {
        
        ZStack {
            switch onEnum(of: uiState) {
                case .initialSeasonsState: empty
                case .seasonDetailsLoaded(let state): SeasonDetailsContent(state)
                case .seasonDetailsErrorState: ErrorUiView(
                    systemImage: "exclamationmark.triangle.fill",
                    action: { component.dispatch(action: ReloadSeasonDetails()) }
                )
            }
        }
        .ignoresSafeArea()
        .sheet(isPresented: $showModal) {
            if let uiState = uiState as? SeasonDetailsLoaded {
                ImageGalleryContentView(items: uiState.seasonImages)
            }
        }
        
    }
    
    @ViewBuilder
    private func SeasonDetailsContent(_ state: SeasonDetailsLoaded) -> some View {
        ScalingHeaderScrollView {
            HeaderContent(state)
        } content: {
            SeasonOverview(state)
            
            EpisodeListView(
                state: state,
                onEpisodeHeaderClicked: { component.dispatch(action: OnEpisodeHeaderClicked()) },
                onWatchedStateClicked: {
                    component.dispatch(action: UpdateSeasonWatchedState())
                }
            )
            
            CastListView(casts: toCastsList(state.seasonCast))
            
        }
        .height(min: DimensionConstants.minHeight, max: DimensionConstants.imageHeight)
        .collapseProgress($progress)
        .allowsHeaderGrowth()
        .hideScrollIndicators()
        .shadow(radius: progress)
        .onAppear { showModal = state.showSeasonWatchStateDialog }
        
        TopBar(
            progress: progress,
            title: state.seasonName,
            isRefreshing: state.isUpdating,
            onBackClicked: { component.dispatch(action: SeasonDetailsBackClicked()) }, onRefreshClicked: {})
        
    }
    
    @ViewBuilder
    private func HeaderContent(_ content: SeasonDetailsLoaded) -> some View {
        ZStack {
            HeaderCoverArtWorkView(
                backdropImageUrl: content.imageUrl,
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
                        
                        
                        Text("^[\(content.seasonImages.count) Image](inflect: true)")
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
                        component.dispatch(action: SeasonGalleryClicked())
                        showModal.toggle()
                    }
                    
                    ProgressView(value: content.watchProgress, total: 1)
                        .progressViewStyle(RoundedRectProgressViewStyle())
                }
            }
        }
    }
    
    @ViewBuilder
    private func SeasonOverview(_ content: SeasonDetailsLoaded) -> some View {
        VStack(alignment: .leading) {
            Text("Overview")
                .bodyFont(size: 26)
                .fontWeight(.semibold)
                .frame(maxWidth: .infinity, alignment: .leading)
                .padding(.bottom, 8)
                .padding(.bottom, 0.5)
            
            Text(content.seasonOverview)
                .font(.callout)
                .padding([.top], 2)
                .lineLimit(showFullText ? nil : 4)
                .multilineTextAlignment(.leading)
                .background(
                    Text(content.seasonOverview)
                        .lineLimit(4)
                        .font(.callout)
                        .padding([.top], 2)
                        .background(GeometryReader { displayedGeometry in
                            ZStack {
                                Text(content.seasonOverview)
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
    
    private func toCastsList(_ list: [Cast]) -> [Casts] {
        return list.map{ (cast) -> Casts in
            Casts(id: cast.id, name: cast.name, profileUrl: cast.profileUrl, characterName: cast.characterName)
        }
    }
    
    @ViewBuilder
    private var empty: some View {
        if #available(iOS 17.0, *) {
            ContentUnavailableView(
                "Please wait while we get your content.",
                systemImage: "rectangle.on.rectangle"
            )
            .padding()
            .multilineTextAlignment(.center)
            .font(.callout)
            .foregroundColor(.secondary)
        } else {
            FullScreenView(
                systemName: "rectangle.on.rectangle",
                message: "Please wait while we get your content."
            )
        }
    }
}


private struct DimensionConstants {
    static let imageHeight: CGFloat = 320
    static let minHeight: CGFloat = 120.0
}
