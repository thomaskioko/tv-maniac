//
//  SeasonDetailsView.swift
//  tv-maniac
//
//  Created by Thomas Kioko on 21.12.23.
//  Copyright © 2023 orgName. All rights reserved.
//

import SwiftUI
import SwiftUIComponents
import TvManiac

struct SeasonDetailsView: View {
    private let component: SeasonDetailsComponent
    
    @Environment(\.presentationMode) var presentationMode
    
    @StateFlow private var uiState: SeasonDetailState
    @State private var isTruncated = false
    @State private var showFullText = false
    @State private var showModal = false
    @State private var scrollOffset: CGFloat = 0
    
    init(component: SeasonDetailsComponent) {
        self.component = component
        _uiState = StateFlow(component.state)
    }
    
    var body: some View {
        ZStack {
            Color.background.edgesIgnoringSafeArea(.all)
            
            switch onEnum(of: uiState) {
                case .initialSeasonsState: LoadingIndicatorView(animate: true)
                case .seasonDetailsLoaded(let state): SeasonDetailsContent(state)
                case .seasonDetailsErrorState:
                    FullScreenView(
                        systemName: "exclamationmark.triangle.fill",
                        message: "Something went wrong",
                        buttonText: "Retry",
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
        ParallaxView(
            title: state.seasonName,
            isRefreshing: state.isUpdating,
            imageHeight: DimensionConstants.imageHeight,
            collapsedImageHeight: DimensionConstants.collapsedImageHeight,
            header: { proxy in
                HeaderContent(
                    state: state,
                    progress: proxy.getTitleOpacity(
                        geometry: proxy,
                        imageHeight: DimensionConstants.imageHeight,
                        collapsedImageHeight: DimensionConstants.collapsedImageHeight
                    ),
                    headerHeight: proxy.getHeightForHeaderImage(proxy)
                )
            },
            content: { titleRect in

                OverviewBoxView(
                    overview: state.seasonOverview,
                    titleRect: titleRect
                )
                    .padding()

                EpisodeListView(
                    episodeCount: state.episodeCount,
                    watchProgress: state.watchProgress,
                    expandEpisodeItems: state.expandEpisodeItems,
                    showSeasonWatchStateDialog: state.showSeasonWatchStateDialog,
                    isSeasonWatched: state.isSeasonWatched,
                    items: state.episodeDetailsList,
                    onEpisodeHeaderClicked: { component.dispatch(action: OnEpisodeHeaderClicked()) },
                    onWatchedStateClicked: { component.dispatch(action: UpdateSeasonWatchedState()) }
                )
                
                CastListView(casts: toCastsList(state.seasonCast))
            },
            onBackClicked: {
                component.dispatch(action: SeasonDetailsBackClicked())
            },
            onRefreshClicked: {}
        )
        .onAppear { showModal = state.showSeasonWatchStateDialog }
    }

    @ViewBuilder
    private func HeaderContent(state: SeasonDetailsLoaded, progress: CGFloat, headerHeight: CGFloat) -> some View {
        ZStack(alignment: .bottom) {
            HeaderCoverArtWorkView(
                backdropImageUrl: state.imageUrl,
                posterHeight: headerHeight
            )
            .foregroundStyle(.ultraThinMaterial)
            .overlay(
                LinearGradient(
                    gradient: Gradient(colors: [
                        .clear,
                        .clear,
                        .clear,
                        Color.background.opacity(0.6),
                        Color.background.opacity(0.8),
                        Color.background,
                    ]),
                    startPoint: .top,
                    endPoint: .bottom
                )
            )
            .frame(height: headerHeight)
            
            ZStack(alignment: .bottom) {
                VStack {
                    Spacer()
                    HStack(spacing: 16) {
                        Image(systemName: "photo.fill.on.rectangle.fill")
                            .resizable()
                            .frame(width: 28.0, height: 28.0)
                            .fontDesign(.rounded)
                            .font(.callout)
                            .fontWeight(.regular)
                            .foregroundColor(.secondary)
                            .alignmentGuide(.view) { d in d[HorizontalAlignment.leading] }
                        
                        Text("^[\(state.seasonImages.count) Image](inflect: true)")
                            .bodyMediumFont(size: 16)
                            .foregroundColor(.textColor)
                            .lineLimit(1)
                            .alignmentGuide(.view) { d in d[HorizontalAlignment.center] }
                        
                        Spacer()
                    }
                    .padding(16)
                    .contentShape(Rectangle())
                    .onTapGesture {
                        component.dispatch(action: SeasonGalleryClicked())
                        showModal.toggle()
                    }
                }
                .frame(height: headerHeight)
                .opacity(1 - progress)
            }
            
            ProgressView(value: state.watchProgress, total: 1)
                .progressViewStyle(RoundedRectProgressViewStyle())
        }
        .frame(height: headerHeight)
        .clipped()
    }
    
    private func toCastsList(_ list: [Cast]) -> [Casts] {
        return list.map { cast -> Casts in
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

private enum DimensionConstants {
    static let imageHeight: CGFloat = 320
    static let collapsedImageHeight: CGFloat = 120.0
}
