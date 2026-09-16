import Components
import DesignSystem
import MoreShows
import SwiftUI
import TvManiac
import TvManiacKit

public struct GenreShowsView: View {
    private let presenter: GenreShowsPresenter
    @StateValue private var uiState: GenreShowsState
    @State private var toast: Toast?

    public init(presenter: GenreShowsPresenter) {
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
                presenter.dispatch(action: GenreShowClicked(showId: id))
            },
            onBack: {
                presenter.dispatch(action: GenreShowsBackClicked())
            },
            onRetry: {
                presenter.dispatch(action: RetryGenreShowsLoadMore())
            }
        )
        .refreshable {
            presenter.dispatch(action: RefreshGenreShows())
        }
        .onChange(of: uiState.errorMessage) { _, message in
            if let message {
                toast = Toast(type: .error, message: message)
            }
        }
        .onChange(of: toast) { _, newValue in
            if newValue == nil, uiState.errorMessage != nil {
                presenter.dispatch(action: DismissGenreShowsError())
            }
        }
    }
}

private extension GenreShowsState {
    func toState() -> MoreShowsScreen.State {
        MoreShowsScreen.State(
            title: title,
            items: items.map { $0.toSwift() },
            isLoadingMore: isAppendLoading,
            hasNextPage: hasNextPage,
            loadError: appendError,
            retryLabel: String(\.button_error_retry)
        )
    }
}
