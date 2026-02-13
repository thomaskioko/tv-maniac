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

    @StateObject @KotlinStateFlow private var uiState: SeasonDetailsModel
    @State private var isTruncated = false
    @State private var showFullText = false
    @State private var showModal = false
    @State private var showGlass: Double = 0
    @State private var progressViewOffset: CGFloat = 0
    @State private var showMarkPreviousAlert = false
    @State private var showUnwatchedConfirmAlert = false
    @State private var showMarkPreviousSeasonsAlert = false
    @State private var showSeasonUnwatchAlert = false
    @State private var toast: Toast?

    init(presenter: SeasonDetailsPresenter) {
        self.presenter = presenter
        _uiState = .init(presenter.state)
    }

    var body: some View {
        ZStack {
            theme.colors.background.edgesIgnoringSafeArea(.all)

            if uiState.showError {
                FullScreenView(
                    systemName: "exclamationmark.triangle.fill",
                    message: String(\.generic_error_message),
                    buttonText: String(\.button_error_retry),
                    action: { presenter.dispatch(action: ReloadSeasonDetails()) }
                )
            } else {
                SeasonDetailsContent(uiState)
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
                    isLoading: uiState.isRefreshing,
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
        .animation(.easeInOut(duration: AnimationConstants.defaultDuration), value: showGlass)
        .edgesIgnoringSafeArea(.top)
        .sheet(isPresented: $showModal) {
            ImageGalleryContentView(items: uiState.seasonImages.map {
                $0.toSwift()
            })
        }
        .onChange(of: uiState.message) { _, newValue in
            if let message = newValue {
                toast = Toast(
                    type: .error,
                    title: "Error",
                    message: message.message
                )
                presenter.dispatch(action: SeasonDetailsMessageShown(id: message.id))
            }
        }
        .toastView(toast: $toast)
        .onChange(of: uiState.isDialogVisible) { _ in
            let dialogState = uiState.dialogState
            showMarkPreviousAlert = dialogState is SeasonDialogStateMarkPreviousEpisodesConfirmation
            showUnwatchedConfirmAlert = dialogState is SeasonDialogStateUnwatchEpisodeConfirmation
            showMarkPreviousSeasonsAlert = dialogState is SeasonDialogStateMarkPreviousSeasonsConfirmation
            showSeasonUnwatchAlert = dialogState is SeasonDialogStateUnwatchSeasonConfirmation
        }
        .alert(String(\.dialog_title_unwatched), isPresented: $showSeasonUnwatchAlert) {
            Button(String(\.dialog_button_yes)) {
                presenter.dispatch(action: ConfirmDialogAction())
            }
            Button(String(\.dialog_button_no), role: .cancel) {
                presenter.dispatch(action: DismissDialog())
            }
        } message: {
            Text(String(\.dialog_message_unwatched))
        }
        .alert(String(\.dialog_title_mark_previous), isPresented: $showMarkPreviousAlert) {
            Button(String(\.dialog_button_mark_all)) {
                presenter.dispatch(action: ConfirmDialogAction())
            }
            Button(String(\.dialog_button_just_this), role: .cancel) {
                presenter.dispatch(action: SecondaryDialogAction())
            }
        } message: {
            Text(String(\.dialog_message_mark_previous))
        }
        .alert(String(\.dialog_title_episode_unwatched), isPresented: $showUnwatchedConfirmAlert) {
            Button(String(\.dialog_button_yes)) {
                presenter.dispatch(action: ConfirmDialogAction())
            }
            Button(String(\.dialog_button_no), role: .cancel) {
                presenter.dispatch(action: DismissDialog())
            }
        } message: {
            Text(String(\.dialog_message_episode_unwatched))
        }
        .alert(String(\.dialog_title_mark_previous_seasons), isPresented: $showMarkPreviousSeasonsAlert) {
            Button(String(\.dialog_button_mark_all_seasons)) {
                presenter.dispatch(action: ConfirmDialogAction())
            }
            Button(String(\.dialog_button_just_this_season), role: .cancel) {
                presenter.dispatch(action: SecondaryDialogAction())
            }
        } message: {
            Text(String(\.dialog_message_mark_previous_seasons))
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
                    title: String(\.title_episodes),
                    episodeCount: state.episodeCount,
                    watchProgress: state.watchProgress,
                    expandEpisodeItems: state.expandEpisodeItems,
                    showSeasonWatchStateDialog: state.dialogState is SeasonDialogStateUnwatchSeasonConfirmation,
                    isSeasonWatched: state.isSeasonWatched,
                    items: state.episodeDetailsList.map {
                        $0.toSwift()
                    },
                    dayLabelFormat: { count in String(\.day_label, quantity: count) },
                    onEpisodeHeaderClicked: { presenter.dispatch(action: OnEpisodeHeaderClicked()) },
                    onWatchedStateClicked: {
                        presenter.dispatch(action: ToggleSeasonWatched())
                    },
                    onEpisodeWatchToggle: { episode in
                        presenter.dispatch(action: ToggleEpisodeWatched(episodeId: episode.episodeId))
                    }
                )

                Spacer().frame(height: theme.spacing.large)

                CastListView(casts: toCastsList(state.seasonCast))
            },
            onScroll: { offset in
                showGlass = ParallaxConstants.glassOpacity(from: offset)

                let startOffset = CGFloat(245)
                let endOffset = 0
                progressViewOffset = max(CGFloat(endOffset), startOffset + offset)
            }
        )
        .onAppear {
            showModal = state.isGalleryVisible
        }
        .onDisappear {
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
                        presenter.dispatch(action: ShowGallery())
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
