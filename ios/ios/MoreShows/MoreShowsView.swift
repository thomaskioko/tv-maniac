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
import TvManiacUI

struct MoreShowsView: View {
    private let component: MoreShowsComponent

    @StateFlow private var uiState: MoreShowsState
    @State private var query = String()

    init(component: MoreShowsComponent) {
        self.component = component
        _uiState = StateFlow(component.state)
    }

    var body: some View {
        NavigationView {
            VStack(spacing: 0) {
                NavigationTopBar(
                    topBarTitle: uiState.categoryTitle,
                    onBackClicked: { component.dispatch(action: MoreBackClicked()) }
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
            component.getElement(index: Int32(index))
        }

        GridView(
            items: shows.map { $0.toSwift() },
            onAction: { id in
                component.dispatch(action: MoreShowClicked(showId: id))
            }
        )
    }
}

private enum DimensionConstants {
    static let posterColumns = [GridItem(.adaptive(minimum: 100), spacing: 4)]
    static let spacing: CGFloat = 4
}
