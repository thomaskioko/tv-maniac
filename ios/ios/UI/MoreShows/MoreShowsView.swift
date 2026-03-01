import SwiftUI
import SwiftUIComponents
import TvManiac
import TvManiacKit

struct MoreShowsView: View {
    private let presenter: MoreShowsPresenter
    @StateObject @KotlinStateFlow private var uiState: MoreShowsState
    @State private var toast: Toast?
    @State private var items: [ShowPosterImage] = []

    init(presenter: MoreShowsPresenter) {
        self.presenter = presenter
        _uiState = .init(presenter.state)
    }

    var body: some View {
        MoreShowsScreen(
            title: uiState.categoryTitle ?? "",
            items: items,
            isLoadingMore: uiState.isAppendLoading,
            loadError: uiState.appendError,
            toast: $toast,
            onItemAppear: { index in
                presenter.onItemVisible(index: Int32(index))
            },
            onAction: { id in
                presenter.dispatch(action: MoreShowClicked(traktId: id))
            },
            onBack: {
                presenter.dispatch(action: MoreBackClicked())
            },
            onRetry: {
                presenter.dispatch(action: RetryLoadMore())
            }
        )
        .onAppear {
            items = uiState.items.map { $0.toSwift() }
        }
        .onChange(of: uiState.items) { newItems in
            items = newItems.map { $0.toSwift() }
        }
        .refreshable {
            presenter.dispatch(action: RefreshMoreShows())
        }
        .onChange(of: uiState.errorMessage) { _, message in
            if let message {
                toast = Toast(type: .error, message: message)
            }
        }
        .onChange(of: toast) { _, newValue in
            if newValue == nil, uiState.errorMessage != nil {
                presenter.dispatch(action: DismissErrorMessage())
            }
        }
    }
}
