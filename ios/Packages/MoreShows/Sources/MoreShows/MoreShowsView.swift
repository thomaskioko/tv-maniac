import Components
import DesignSystem
import SwiftUI
import TvManiac
import TvManiacKit

public struct MoreShowsView: View {
    private let presenter: MoreShowsPresenter
    @StateValue private var uiState: MoreShowsState
    @State private var toast: Toast?

    public init(presenter: MoreShowsPresenter) {
        self.presenter = presenter
        _uiState = .init(presenter.stateValue)
    }

    public var body: some View {
        MoreShowsScreen(
            state: uiState.toState(),
            toast: $toast,
            onItemAppear: { index in
                presenter.onItemVisible(index: Int32(index))
            },
            onLoadMore: {
                presenter.loadMore()
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

private extension MoreShowsState {
    func toState() -> MoreShowsScreen.State {
        MoreShowsScreen.State(
            title: categoryTitle ?? "",
            items: items.map { $0.toSwift() },
            isLoadingMore: isAppendLoading,
            hasNextPage: hasNextPage,
            loadError: appendError,
            retryLabel: String(\.button_error_retry)
        )
    }
}
