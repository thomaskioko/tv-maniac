//
//  SeasonDetailsView.swift
//  tv-maniac
//
//  Created by Thomas Kioko on 21.12.23.
//  Copyright © 2023 orgName. All rights reserved.
//

import SwiftUI
import SwiftUIComponents
import TvManiacKit

struct SeasonDetailsView: View {
    @Theme private var theme

    private let presenter: SeasonDetailsPresenter

    @Environment(\.presentationMode) var presentationMode

    @StateFlow private var uiState: SeasonDetailsModel
    @State private var isTruncated = false
    @State private var showFullText = false
    @State private var showModal = false
    @State private var showGlass: Double = 0
    @State private var progressViewOffset: CGFloat = 0

    init(presenter: SeasonDetailsPresenter) {
        self.presenter = presenter
        _uiState = StateFlow(presenter.state)
    }

    var body: some View {
        ZStack {
            theme.colors.background.edgesIgnoringSafeArea(.all)

            if uiState.message == nil {
                SeasonDetailsContent(uiState)
            } else {
                FullScreenView(
                    systemName: "exclamationmark.triangle.fill",
                    message: String(\.generic_error_message),
                    buttonText: String(\.button_error_retry),
                    action: { presenter.dispatch(action: ReloadSeasonDetails()) }
                )
            }
        }
        .ignoresSafeArea()
        .navigationBarTitleDisplayMode(.inline)
        .navigationBarBackButtonHidden(true)
        .navigationBarColor(backgroundColor: .clear)
        .overlay(
            VStack(spacing: 0) {
                GlassToolbar(
                    title: uiState.seasonName,
                    opacity: showGlass,
                    leadingIcon: {
                        Button(action: {
                            presenter.dispatch(action: SeasonDetailsBackClicked())
                        }) {
                            Image(systemName: "chevron.left")
                                .foregroundColor(theme.colors.accent)
                                .imageScale(.large)
                                .opacity(1 - showGlass)
                        }
                    }
                )
                ProgressView(value: uiState.watchProgress, total: 1)
                    .progressViewStyle(RoundedRectProgressViewStyle())
                    .offset(y: progressViewOffset)
            },
            alignment: .top
        )
        .animation(.easeInOut(duration: 0.25), value: showGlass)
        .edgesIgnoringSafeArea(.top)
        .sheet(isPresented: $showModal) {
            ImageGalleryContentView(items: uiState.seasonImages.map {
                $0.toSwift()
            })
        }
    }

    @ViewBuilder
    private func SeasonDetailsContent(_ state: SeasonDetailsModel) -> some View {
        ParallaxView(
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
            content: {
                if !state.seasonOverview.isEmpty {
                    Text(String(\.title_season_overview))
                        .textStyle(theme.typography.titleLarge)
                        .foregroundColor(theme.colors.onSurface)
                        .lineLimit(1)
                        .padding(.top, theme.spacing.large)
                        .padding(.horizontal)
                        .frame(maxWidth: .infinity, alignment: .leading)

                    OverviewBoxView(
                        overview: state.seasonOverview
                    )
                    .padding()
                }

                EpisodeListView(
                    episodeCount: state.episodeCount,
                    watchProgress: state.watchProgress,
                    expandEpisodeItems: state.expandEpisodeItems,
                    showSeasonWatchStateDialog: false,
                    isSeasonWatched: state.isSeasonWatched,
                    items: state.episodeDetailsList.map {
                        $0.toSwift()
                    },
                    onEpisodeHeaderClicked: { presenter.dispatch(action: OnEpisodeHeaderClicked()) },
                    onWatchedStateClicked: {
//                        presenter.dispatch(action: UpdateSeasonWatchedState())
                    }
                )

                CastListView(casts: toCastsList(state.seasonCast))
            },
            onScroll: { offset in
                let opacity = -offset - 150
                let normalizedOpacity = opacity / 200
                showGlass = max(0, min(1, normalizedOpacity))

                let startOffset = CGFloat(245)
                let endOffset = 0
                progressViewOffset = max(CGFloat(endOffset), startOffset + offset)
            }
        )
        .onAppear {
            showModal = false
        }
    }

    @ViewBuilder
    private func HeaderContent(state: SeasonDetailsModel, progress: CGFloat, headerHeight: CGFloat) -> some View {
        ZStack(alignment: .bottom) {
            HeaderCoverArtWorkView(
                imageUrl: state.imageUrl,
                posterHeight: headerHeight
            )
            .foregroundStyle(.ultraThinMaterial)
            .overlay(
                LinearGradient(
                    gradient: Gradient(colors: [
                        .clear,
                        .clear,
                        .clear,
                        .clear,
                        .clear,
                        theme.colors.background.opacity(0.8),
                        theme.colors.background,
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
                            .textStyle(theme.typography.bodyMedium)
                            .fontWeight(.regular)
                            .foregroundColor(theme.colors.onSurfaceVariant)
                            .alignmentGuide(.view) { d in
                                d[HorizontalAlignment.leading]
                            }

                        Text(String(\.season_images_count, quantity: state.seasonImages.count))
                            .textStyle(theme.typography.bodyMedium)
                            .foregroundColor(theme.colors.onSurface)
                            .lineLimit(1)
                            .alignmentGuide(.view) { d in
                                d[HorizontalAlignment.center]
                            }

                        Spacer()
                    }
                    .padding(.horizontal, theme.spacing.medium)
                    .padding(.vertical, theme.spacing.xLarge)
                    .contentShape(Rectangle())
                    .onTapGesture {
//                        presenter.dispatch(action: SeasonGalleryClicked())
                        showModal.toggle()
                    }
                }
                .frame(height: headerHeight)
            }
            .opacity(1 - progress)
        }
        .frame(height: headerHeight)
        .clipped()
    }

    private func toCastsList(_ list: [Cast]) -> [SwiftCast] {
        list.map { cast -> SwiftCast in
            .init(castId: cast.id, name: cast.name, characterName: cast.characterName, profileUrl: cast.profileUrl)
        }
    }
}

private enum DimensionConstants {
    static let imageHeight: CGFloat = 350
    static let collapsedImageHeight: CGFloat = 120.0
}
