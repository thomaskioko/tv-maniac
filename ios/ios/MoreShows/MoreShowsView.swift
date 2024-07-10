//
//  MoreShowsView.swift
//  tv-maniac
//
//  Created by Thomas Kioko on 02.01.24.
//  Copyright © 2024 orgName. All rights reserved.
//

import SwiftUI
import TvManiac

struct MoreShowsView: View {

    private let presenter: MoreShowsPresenter

    @ObservedObject
    private var uiState: StateFlow<MoreShowsState>

    @State
    private var query = String()

    init(presenter: MoreShowsPresenter) {
        self.presenter = presenter
        self.uiState = StateFlow<MoreShowsState>(presenter.state)
    }

    var body: some View {
        NavigationView {
            VStack(spacing: 0) {

                NavigationTopBar(
                    topBarTitle: uiState.value?.categoryTitle ?? "",
                    imageName: "arrow.backward",
                    onBackClicked: { presenter.dispatch(action: MoreBackClicked()) }
                )

                Spacer().frame(height: 10)

                ShowsContent(uiState.value!)
            }
            .edgesIgnoringSafeArea(.top)
        }
    }

    @ViewBuilder
    private func ShowsContent(_ state: MoreShowsState) -> some View {
        ScrollView(.vertical, showsIndicators: false) {
            LazyVGrid(columns: DimensionConstants.posterColumns,spacing: DimensionConstants.spacing) {
                ForEach(state.snapshotList.indices, id: \.self){ index in
                    if let show = presenter.getElement(index: Int32(index)){
                        PosterItemView(
                            showId: show.tmdbId,
                            title: show.title,
                            posterUrl: show.posterImageUrl,
                            posterWidth: 130,
                            posterHeight: 200
                        )
                        .aspectRatio(contentMode: .fill)
                        .frame(minWidth: 0, maxWidth: .infinity, minHeight: 0, maxHeight: .infinity)
                        .clipped()
                        .listRowInsets(EdgeInsets())
                        .listRowBackground(Color.clear)
                        .onTapGesture { presenter.dispatch(action: MoreShowClicked(showId: show.tmdbId)) }
                    }
                }
            }
            .padding(2)
        }
    }
}

private struct DimensionConstants {
    static let posterColumns = [GridItem(.adaptive(minimum: 100), spacing: 4)]
    static let spacing: CGFloat = 4
}
