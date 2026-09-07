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
            moreOptionsAccessibilityLabel: String(\.cd_list_options),
            onBack: { presenter.dispatch(action: ListDetailActionBackClicked()) },
            onItemAppear: { index in presenter.onItemVisible(index: Int32(index)) },
            onLoadMore: { presenter.loadMore() },
            onRefresh: { presenter.dispatch(action: ListDetailActionRefreshList()) },
            onShowClicked: { tmdbId in presenter.dispatch(action: ListDetailActionShowClicked(tmdbId: tmdbId)) },
            onRemoveRequested: { tmdbId in presenter.dispatch(action: ListDetailActionRemoveRequested(tmdbId: tmdbId)) },
            onRemoveConfirmed: { presenter.dispatch(action: ListDetailActionRemoveConfirmed()) },
            onRemoveDismissed: { presenter.dispatch(action: ListDetailActionRemoveDismissed()) },
            onRetryLoadMore: { presenter.dispatch(action: ListDetailActionRetryLoadMore()) },
            onDismissErrorMessage: { presenter.dispatch(action: ListDetailActionDismissErrorMessage()) },
            onRenameRequested: { presenter.dispatch(action: ListDetailActionRenameRequested()) },
            onRenameNameChanged: { name in presenter.dispatch(action: ListDetailActionRenameNameChanged(name: name)) },
            onRenameConfirmed: { presenter.dispatch(action: ListDetailActionRenameConfirmed()) },
            onRenameDismissed: { presenter.dispatch(action: ListDetailActionRenameDismissed()) },
            onDeleteRequested: { presenter.dispatch(action: ListDetailActionDeleteRequested()) },
            onDeleteConfirmed: { presenter.dispatch(action: ListDetailActionDeleteConfirmed()) },
            onDeleteDismissed: { presenter.dispatch(action: ListDetailActionDeleteDismissed()) }
        )
    }
}

private extension ListDetailState {
    func toState() -> ListDetailScreen.State {
        ListDetailScreen.State(
            title: title,
            isLoading: isRefreshLoading,
            canRefresh: canRefresh,
            emptyMessage: emptyMessage,
            errorMessage: errorMessage,
            dismissErrorLabel: String(\.label_ok),
            items: items.map { $0.toSwift() },
            isLoadingMore: isAppendLoading,
            loadError: appendError,
            retryLabel: String(\.button_error_retry),
            removeButtonLabel: String(\.list_detail_remove_button),
            removeConfirmation: removeConfirmation?.toSwift(),
            cancelLabel: String(\.label_cancel),
            renameLabel: renameLabel,
            deleteLabel: deleteLabel,
            renameDialog: renameDialog?.toSwift(),
            deleteConfirmation: deleteConfirmation?.toSwift()
        )
    }
}
