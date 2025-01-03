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
import TvManiacKit
import TvManiacUI

struct SeasonDetailsView: View {
  private let presenter: SeasonDetailsPresenter

  @Environment(\.presentationMode) var presentationMode

  @StateFlow private var uiState: SeasonDetailState
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
      Color.background.edgesIgnoringSafeArea(.all)

      switch onEnum(of: uiState) {
      case .initialSeasonsState:
        CenteredFullScreenView {
          LoadingIndicatorView(animate: true)
        }
      case let .seasonDetailsLoaded(state):
        SeasonDetailsContent(state)
      case .seasonDetailsErrorState:
        FullScreenView(
          systemName: "exclamationmark.triangle.fill",
          message: "Something went wrong",
          buttonText: "Retry",
          action: { presenter.dispatch(action: ReloadSeasonDetails()) }
        )
      }
    }
    .ignoresSafeArea()
    .navigationBarTitleDisplayMode(.inline)
    .navigationBarColor(backgroundColor: .clear)
    .overlay(
      VStack(spacing: 0) {
        GlassToolbar(title: (uiState as? SeasonDetailsLoaded)?.seasonName ?? "", opacity: showGlass)
        if let state = uiState as? SeasonDetailsLoaded {
          ProgressView(value: state.watchProgress, total: 1)
            .progressViewStyle(RoundedRectProgressViewStyle())
            .offset(y: progressViewOffset)
        }
      },
      alignment: .top
    )
    .animation(.easeInOut(duration: 0.25), value: showGlass)
    .edgesIgnoringSafeArea(.top)
    .sheet(isPresented: $showModal) {
      if let uiState = uiState as? SeasonDetailsLoaded {
        ImageGalleryContentView(items: uiState.seasonImages.map { $0.toSwift() })
      }
    }
  }

  @ViewBuilder
  private func SeasonDetailsContent(_ state: SeasonDetailsLoaded) -> some View {
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
        if state.seasonOverview != nil, !state.seasonOverview.isEmpty {
          Text("Season Overview")
            .font(.title3)
            .fontWeight(.bold)
            .foregroundColor(.textColor)
            .lineLimit(1)
            .padding(.top, 24)
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
          showSeasonWatchStateDialog: state.showSeasonWatchStateDialog,
          isSeasonWatched: state.isSeasonWatched,
          items: state.episodeDetailsList.map { $0.toSwift() },
          onEpisodeHeaderClicked: { presenter.dispatch(action: OnEpisodeHeaderClicked()) },
          onWatchedStateClicked: { presenter.dispatch(action: UpdateSeasonWatchedState()) }
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
    .onAppear { showModal = state.showSeasonWatchStateDialog }
  }

  @ViewBuilder
  private func HeaderContent(state: SeasonDetailsLoaded, progress: CGFloat, headerHeight: CGFloat) -> some View {
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
            presenter.dispatch(action: SeasonGalleryClicked())
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
    return list.map { cast -> SwiftCast in
      .init(castId: cast.id, name: cast.name, characterName: cast.characterName, profileUrl: cast.profileUrl)
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
  static let imageHeight: CGFloat = 350
  static let collapsedImageHeight: CGFloat = 120.0
}