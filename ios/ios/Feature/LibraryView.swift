//
//  WatchlistView.swift
//  tv-maniac
//
//  Created by Thomas Kioko on 19.08.21.
//  Copyright © 2021 orgName. All rights reserved.
//

import SwiftUI
import TvManiac

struct LibraryView: View {
    
    private let presenter: LibraryPresenter

    @ObservedObject
    private var uiState: StateFlow<LibraryState>

    init(presenter: LibraryPresenter){
        self.presenter = presenter
        self.uiState = StateFlow<LibraryState>(presenter.state)
    }
    
    var body: some View {
        NavigationStack {
            VStack {
                switch onEnum(of: uiState.value) {
                    case .loadingShows:
                    //TODO:: Show indicator on the toolbar
                    LoadingIndicatorView()
                        .frame(maxWidth: UIScreen.main.bounds.width, maxHeight: UIScreen.main.bounds.height,  alignment: .center)
                    case .libraryContent(let content): GridViewContent(content)
                    case .errorLoadingShows: EmptyView()  //TODO:: Show Error
                    case .none: EmptyView()
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
     
    }
    
    @ViewBuilder
    private func GridViewContent(_ content: LibraryContent) -> some View {
        if !content.list.isEmpty {
            ScrollView(.vertical, showsIndicators: false) {
                LazyVGrid(columns: DimensionConstants.posterColumns,spacing: DimensionConstants.spacing){
                    ForEach(content.list, id: \.tmdbId){ item in
                        PosterItemView(
                            showId: item.tmdbId,
                            title: item.title,
                            posterUrl: item.posterImageUrl,
                            posterWidth: 130,
                            posterHeight: 200
                        )
                        .aspectRatio(contentMode: .fill)
                        .frame(minWidth: 0, maxWidth: .infinity, minHeight: 0, maxHeight: .infinity)
                        .clipped()
                        .onTapGesture { presenter.dispatch(action: LibraryShowClicked(id: item.tmdbId)) }
                    }
                }.padding(.all, 10)
            }
        } else {
            empty
        }
    }
    
    private var filterButton: some View {
        Button {
            withAnimation {
                //TODO:: Show Filter menu
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
           //TODO:: Add filer option
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

private struct DimensionConstants {
    static let posterColumns = [GridItem(.adaptive(minimum: 100), spacing: 4)]
    static let spacing: CGFloat = 4
}
