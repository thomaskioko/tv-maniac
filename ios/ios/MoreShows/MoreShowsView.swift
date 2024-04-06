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

    @ObservedObject private var viewModel: ViewModel

    private let presenter: MoreShowsPresenter
    init(presenter: MoreShowsPresenter) {
        self.presenter = presenter
        self.viewModel = ViewModel(presenter: presenter)
    }

    var body: some View {
        NavigationStack {
            VStack {
                MoreContent()
            }
            .navigationBarTitleDisplayMode(.inline)
            .background(Color.background)
            .toolbar {
                ToolbarItem(placement: .navigationBarLeading) {
                    Button
                    {
                        presenter.dispatch(action: MoreBackClicked())
                    } label: {
                        Text(viewModel.categoryTitle ?? "")
                    }
                    .buttonStyle(CircleButtonStyle(imageName: "arrow.backward"))
                    .padding(.top)
                }
            }
        }
        .task {
            await viewModel.startLoading()
        }.task {
            await viewModel.subscribeDataChanged()
        }.task {
            await viewModel.subscribeLoadState()
        }
    }

    @ViewBuilder
    private func MoreContent() -> some View {
        List {
            LazyVGrid(columns: DimensionConstants.posterColumns,spacing: DimensionConstants.spacing) {
                ForEach(viewModel.items, id: \.tmdbId){ item in
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
                    .listRowInsets(EdgeInsets())
                    .listRowBackground(Color.clear)
                    .onTapGesture { presenter.dispatch(action: MoreShowClicked(showId: item.tmdbId)) }
                }
            }
            .listRowInsets(EdgeInsets())
            .listRowBackground(Color.clear)
            .padding(.all, 10)

            if viewModel.showLoading {
                LoadingIndicatorView()
            }

            if (!viewModel.items.isEmpty) {
                VStack(alignment: .center) {
                    if(viewModel.hasNextPage) {
                        ProgressView()
                            .onAppear {
                            viewModel.loadNextPage()
                        }
                    } else {
                        Text("-- Not more --").foregroundColor(.gray)
                    }
                }
                .frame(maxWidth: .infinity).listRowInsets(EdgeInsets())
                    .listRowBackground(Color.clear)
            }

            if let errorMessage = viewModel.errorMessage {
                //TODO:: Show toast
            }

        }
        .listStyle(.plain)
    }

    @ViewBuilder
    private var empty: some View {
        if #available(iOS 17.0, *) {
            ContentUnavailableView(
                "Under Construction",
                systemImage: "figure.walk.motion.trianglebadge.exclamationmark"
            )
            .padding()
            .multilineTextAlignment(.center)
            .font(.callout)
            .foregroundColor(Color.accent)
        } else {
            FullScreenView(
                systemName: "figure.walk.motion.trianglebadge.exclamationmark",
                message: "Under Construction"
            )
        }
    }
}

private struct DimensionConstants {
    static let posterColumns = [GridItem(.adaptive(minimum: 100), spacing: 4)]
    static let spacing: CGFloat = 4
}
