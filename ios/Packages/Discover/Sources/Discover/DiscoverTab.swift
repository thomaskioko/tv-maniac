import Components
import DesignSystem
import SwiftUI
import TvManiacKit

public struct DiscoverTab: View {
    private let presenter: DiscoverShowsPresenter
    @StateValue private var hostState: DiscoverViewState
    @State private var toast: Toast?

    public init(presenter: DiscoverShowsPresenter) {
        self.presenter = presenter
        _hostState = .init(presenter.stateValue)
    }

    public var body: some View {
        DiscoverScreen(
            state: DiscoverScreen.State(
                title: String(\.label_discover_title),
                isLoading: hostState.isLoading,
                isEmpty: hostState.isEmpty,
                showError: hostState.showError,
                errorMessage: hostState.message?.message,
                isRefreshing: hostState.isRefreshing,
                emptyContentText: String(\.generic_empty_content),
                missingApiKeyText: String(\.missing_api_key),
                retryText: String(\.button_error_retry)
            ),
            presenter: presenter,
            toast: $toast,
            onSearchClicked: { presenter.dispatch(action: SearchIconClicked()) },
            onRefresh: { presenter.dispatch(action: RefreshData()) }
        )
        .onChange(of: hostState.message) { _, newValue in
            if let message = newValue {
                toast = Toast(type: .error, title: "Error", message: message.message)
                presenter.dispatch(action: MessageShown(id: message.id))
            }
        }
    }
}
