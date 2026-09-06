import SwiftUI
import TvManiac
import TvManiacKit

public struct ListDetailView: View {
    private let presenter: ListDetailPresenter
    @StateValue private var uiState: ListDetailState

    public init(presenter: ListDetailPresenter) {
        self.presenter = presenter
        _uiState = .init(presenter.stateValue)
    }

    public var body: some View {
        ListDetailScreen(
            state: uiState.toState(),
            backButtonAccessibilityLabel: String(\.cd_back),
            onBack: { presenter.dispatch(action: ListDetailActionBackClicked()) },
            onItemAppear: { index in presenter.onItemVisible(index: Int32(index)) },
            onLoadMore: { presenter.loadMore() },
            onShowClicked: { tmdbId in presenter.dispatch(action: ListDetailActionShowClicked(tmdbId: tmdbId)) },
            onRemoveRequested: { tmdbId in presenter.dispatch(action: ListDetailActionRemoveRequested(tmdbId: tmdbId)) },
            onRemoveConfirmed: { presenter.dispatch(action: ListDetailActionRemoveConfirmed()) },
            onRemoveDismissed: { presenter.dispatch(action: ListDetailActionRemoveDismissed()) },
            onRetryLoadMore: { presenter.dispatch(action: ListDetailActionRetryLoadMore()) },
            onDismissErrorMessage: { presenter.dispatch(action: ListDetailActionDismissErrorMessage()) }
        )
    }
}

private extension ListDetailState {
    func toState() -> ListDetailScreen.State {
        ListDetailScreen.State(
            title: title,
            isLoading: isRefreshLoading,
            emptyMessage: emptyMessage,
            errorMessage: errorMessage,
            dismissErrorLabel: String(\.label_ok),
            items: items.map { $0.toSwift() },
            isLoadingMore: isAppendLoading,
            loadError: appendError,
            retryLabel: String(\.button_error_retry),
            removeButtonLabel: String(\.list_detail_remove_button),
            removeConfirmation: removeConfirmation?.toSwift(),
            cancelLabel: String(\.label_cancel)
        )
    }
}
