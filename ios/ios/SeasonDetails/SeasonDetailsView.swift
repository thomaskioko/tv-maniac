//
//  SeasonDetailsView.swift
//  tv-maniac
//
//  Created by Thomas Kioko on 21.12.23.
//  Copyright © 2023 orgName. All rights reserved.
//

import SwiftUI
import TvManiac

struct SeasonDetailsView: View {
    
    private let component: SeasonDetailsComponent
    
    @Environment(\.presentationMode) var presentationMode
    
    @StateFlow private var uiState: SeasonDetailState
    @State private var isTruncated = false
    @State private var showFullText = false
    @State private var showModal =  false
    @State private var scrollOffset: CGFloat = 0
    @State private var titleRect: CGRect = .zero
    
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
                SeasonOverview(state, titleRect: $titleRect)
                
                EpisodeListView(
                    state: state,
                    onEpisodeHeaderClicked: { component.dispatch(action: OnEpisodeHeaderClicked()) },
                    onWatchedStateClicked: {
                        component.dispatch(action: UpdateSeasonWatchedState())
                    }
                )
                
                CastListView(casts: toCastsList(state.seasonCast))
            },
            onBackClicked: {
                component.dispatch(action: SeasonDetailsBackClicked())
            },
            onRefreshClicked: {
                
            }
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
                            .foregroundColor(.text_color_bg)
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
    
    @ViewBuilder
    private func SeasonOverview(
        _ content: SeasonDetailsLoaded,
        titleRect:  Binding<CGRect>
    ) -> some View {
        VStack(alignment: .leading) {
            
            Spacer(minLength: nil)
                .background(GeometryGetter(rect: self.$titleRect))
            
            Text("Overview")
                .bodyFont(size: 26)
                .fontWeight(.semibold)
                .frame(maxWidth: .infinity, alignment: .leading)
                .padding([.bottom, .top], 8)
            
            Text(content.seasonOverview)
                .font(.callout)
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
        .padding(.horizontal)
        
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
    static let collapsedImageHeight: CGFloat = 120.0
}
