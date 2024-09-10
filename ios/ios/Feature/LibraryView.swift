//
//  WatchlistView.swift
//  tv-maniac
//
//  Created by Thomas Kioko on 19.08.21.
//  Copyright © 2021 orgName. All rights reserved.
//

import SwiftUI
import SwiftUIComponents
import TvManiac
import TvManiacUI

struct LibraryView: View {
    private let component: LibraryComponent

    @StateFlow private var uiState: LibraryState
    
    init(component: LibraryComponent) {
        self.component = component
        _uiState = StateFlow(component.state)
    }
    
    var body: some View {
        VStack {
            switch onEnum(of: uiState) {
                case .loadingShows:
                    // TODO: Show indicator on the toolbar
                    LoadingIndicatorView()
                        .frame(maxWidth: UIScreen.main.bounds.width, maxHeight: UIScreen.main.bounds.height, alignment: .center)
                case .libraryContent(let content): GridViewContent(content)
                case .errorLoadingShows: EmptyView() // TODO: Show Error
            }
        }
        .navigationTitle("Library")
        .navigationBarTitleDisplayMode(.large)
        .background(Color.background)
        .toolbar {
            ToolbarItem(placement: .primaryAction) {
                HStack {
                    filterButton
                    sortButton
                }
            }
        }
    }
    
    @ViewBuilder
    private func GridViewContent(_ content: LibraryContent) -> some View {
        if !content.list.isEmpty {
            GridView(
                items: content.list.map { $0.toSwift() },
                onAction: { id in
                    component.dispatch(action: LibraryShowClicked(id: id))
                }
            )
        } else {
            empty
        }
    }
    
    private var filterButton: some View {
        Button {
            withAnimation {
                // TODO: Show Filter menu
            }
        } label: {
            Label("Sort List", systemImage: "line.3.horizontal.decrease")
                .labelStyle(.iconOnly)
        }
        .buttonBorderShape(.roundedRectangle(radius: 16))
        .buttonStyle(.bordered)
    }
    
    private var sortButton: some View {
        Button {
            // TODO: Add filer option
        } label: {
            Label("Sort Order", systemImage: "arrow.up.arrow.down.circle")
                .labelStyle(.iconOnly)
        }
        .pickerStyle(.navigationLink)
        .buttonBorderShape(.roundedRectangle(radius: 16))
        .buttonStyle(.bordered)
    }
    
    @ViewBuilder
    private var empty: some View {
        if #available(iOS 17.0, *) {
            ContentUnavailableView(
                "Your stash is empty.",
                systemImage: "rectangle.on.rectangle"
            )
            .padding()
            .multilineTextAlignment(.center)
            .font(.callout)
            .foregroundColor(.secondary)
        } else {
            FullScreenView(
                systemName: "rectangle.on.rectangle",
                message: "Your stash is empty."
            )
        }
    }
}

extension TvManiac.LibraryItem {
    func toSwift() -> SwiftShow {
        .init(
            tmdbId: tmdbId,
            title: title,
            posterUrl: posterImageUrl,
            inLibrary: true
        )
    }
}
