import Components
import SwiftUI
import TvManiac
import TvManiacKit

public struct ShowListSheetView: View {
    private let presenter: ShowListPresenter
    @StateValue private var state: ShowListState
    @State private var toast: Toast?

    public init(presenter: ShowListPresenter) {
        self.presenter = presenter
        _state = .init(presenter.stateValue)
    }

    public var body: some View {
        ShowListSheet(
            state: state.toSwift(),
            onDismiss: { presenter.dispatch(action: ShowListActionDismiss()) },
            onShowCreateListField: { presenter.dispatch(action: ShowListActionShowCreateListField()) },
            onToggle: { listId, isCurrentlyInList in
                presenter.dispatch(action: ShowListActionToggleShowInList(
                    listId: listId,
                    isCurrentlyInList: isCurrentlyInList
                ))
            },
            onCreateListNameChanged: { name in
                presenter.dispatch(action: ShowListActionUpdateCreateListName(name: name))
            },
            onCreateListSubmitted: { presenter.dispatch(action: ShowListActionCreateListSubmitted()) }
        )
        .toastView(toast: $toast)
        .onChange(of: state.message) { _, newValue in
            if let message = newValue {
                toast = Toast(type: .error, title: String(\.label_error), message: message.message)
                presenter.dispatch(action: ShowListActionMessageShown(id: message.id))
            }
        }
    }
}
