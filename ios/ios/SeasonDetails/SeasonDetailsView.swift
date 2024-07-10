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

    @Environment(\.presentationMode) var presentationMode
    @ObservedObject private var uiState: StateFlow<SeasonDetailState>

    @State private var progress: CGFloat = 0
    @State private var isTruncated = false
    @State private var showFullText = false
    @State private var showModal =  false

    init(presenter: SeasonDetailsPresenter) {
        self.presenter = presenter
        self.uiState = StateFlow<SeasonDetailsContent>(presenter.state)
    }

    var body: some View {
        if let state = uiState.value {
            ZStack {

                ScalingHeaderScrollView {
                    HeaderContent(state)
                } content: {
                    VStack(alignment: .leading, spacing: 0) {
                        SeasonOverview(state)
                        EpisodeListView(
                            state: state,
                            onEpisodeHeaderClicked: { presenter.dispatch(action: OnEpisodeHeaderClicked()) },
                            onWatchedStateClicked: {
                                presenter.dispatch(action: UpdateSeasonWatchedState())
                            }
                        )
                        CastListView(casts: toCastsList(state.seasonCast))
                    }
                }
                .height(min: DimensionConstants.minHeight, max: DimensionConstants.imageHeight)
                .collapseProgress($progress)
                .allowsHeaderGrowth()
                .hideScrollIndicators()
                .shadow(radius: progress)
                .onAppear { showModal = state.showSeasonWatchStateDialog }

                TopBar(onBackClicked: { presenter.dispatch(action: SeasonDetailsBackClicked()) })
            }
            .ignoresSafeArea()
            .sheet(isPresented: $showModal) {
                ImageGalleryContentView(items: state.seasonImages)
            }
        }
    }

    @ViewBuilder
    private func HeaderContent(_ content: SeasonDetailsContent) -> some View {
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
                        presenter.dispatch(action: SeasonGalleryClicked())
                        showModal.toggle()
                    }

                    ProgressView(value: content.watchProgress, total: 1)
                        .progressViewStyle(RoundedRectProgressViewStyle())
                }

                Text(content.seasonName)
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

    @ViewBuilder
    private func SeasonOverview(_ content: SeasonDetailsContent) -> some View {
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
}


private struct DimensionConstants {
    static let imageHeight: CGFloat = 320
    static let minHeight: CGFloat = 120.0
}
