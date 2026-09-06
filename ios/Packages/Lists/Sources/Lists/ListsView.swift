import SwiftUI
import TvManiac
import TvManiacKit

public struct ListsView: View {
    private let presenter: ListsPresenter
    @StateValue private var uiState: ListsState

    public init(presenter: ListsPresenter) {
        self.presenter = presenter
        _uiState = .init(presenter.stateValue)
    }

    public var body: some View {
        ListsScreen(
            state: uiState.toState(),
            backButtonAccessibilityLabel: String(\.cd_back),
            onBack: { presenter.dispatch(action: ListsActionBackClicked()) },
            onListClicked: { listId in presenter.dispatch(action: ListsActionListClicked(listId: listId)) }
        )
    }
}
