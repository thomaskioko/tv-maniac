//
//  MoreShowsViewModel.swift
//  tv-maniac
//
//  Created by Thomas Kioko on 4/6/24.
//  Copyright © 2024 orgName. All rights reserved.
//

import SwiftUI
import TvManiac

extension MoreShowsView {
    @MainActor final class ViewModel : ObservableObject {

        @Published private(set) var items: [TvShow] = []
        @Published private(set) var categoryTitle: String? = nil
        @Published private(set) var hasNextPage: Bool = false
        @Published private(set) var showLoading: Bool = false
        @Published private(set) var errorMessage: String? = nil
        @ObservedObject @StateFlow private var uiState: MoreShowsState

        private let presenter: MoreShowsPresenter
        private let delegate = SwiftUiPagingHelper<TvShow>()

        init(presenter: MoreShowsPresenter) {
            self.presenter = presenter
            self._uiState = .init(presenter.state)
        }

        func startLoading() async {
            categoryTitle = uiState.categoryTitle

            await uiState.pagingDataFlow.collect { pagingData in
                print(pagingData.description.count)
                try? await skie(delegate).submitData(pagingData: pagingData)
            }
        }

        func subscribeDataChanged() async {
            for await _ in delegate.onPagesUpdatedFlow {
                self.items = delegate.getItems()
            }
        }

        func loadNextPage() {
            delegate.loadNextPage()
        }

        func subscribeLoadState() async {
            for await loadState in delegate.loadStateFlow {
                switch onEnum(of: loadState.append) {
                    case .error(let errorState):
                        print(errorState.error.message?.description ?? "append error...")
                        break
                    case .loading(_):
                        break
                    case .notLoading(let notLoading):
                        self.hasNextPage = !notLoading.endOfPaginationReached
                        break
                }

                switch onEnum(of: loadState.refresh) {
                    case .error(_):
                        break
                    case .loading(_):
                        self.showLoading = true
                        break
                    case .notLoading(_):
                        self.showLoading = false
                        break
                }
            }
        }

    }
}
