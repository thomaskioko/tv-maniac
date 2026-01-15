//
//  MoreShowsView.swift
//  tv-maniac
//
//  Created by Thomas Kioko on 02.01.24.
//  Copyright © 2024 orgName. All rights reserved.
//

import SwiftUI
import SwiftUIComponents
import TvManiac
import TvManiacKit

struct MoreShowsView: View {
    private let presenter: MoreShowsPresenter

    @StateFlow private var uiState: MoreShowsState
    @State private var query = String()

    init(presenter: MoreShowsPresenter) {
        self.presenter = presenter
        _uiState = StateFlow(presenter.state)
    }

    var body: some View {
        NavigationView {
            VStack(spacing: 0) {
                NavigationTopBar(
                    topBarTitle: uiState.categoryTitle,
                    onBackClicked: { presenter.dispatch(action: MoreBackClicked()) }
                )

                Spacer().frame(height: 10)

                ShowsContent(uiState)
            }
            .edgesIgnoringSafeArea(.top)
        }
    }

    @ViewBuilder
    private func ShowsContent(_ state: MoreShowsState) -> some View {
        let shows: [TvShow] = state.snapshotList.indices.compactMap { index in
            presenter.getElement(index: Int32(index))
        }

        GridView(
            items: shows.map {
                $0.toSwift()
            },
            onAction: { id in
                presenter.dispatch(action: MoreShowClicked(traktId: id))
            }
        )
    }
}

private enum DimensionConstants {
    static let posterColumns = [GridItem(.adaptive(minimum: 100), spacing: 4)]
    static let spacing: CGFloat = 4
}
