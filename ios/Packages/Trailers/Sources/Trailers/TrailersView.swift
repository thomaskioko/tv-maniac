import SwiftUI
import TvManiac
import TvManiacKit

public struct TrailersView: View {
    private let presenter: TrailersPresenter
    @StateValue private var uiState: TrailersState
    @Environment(\.dismiss) private var dismiss

    public init(presenter: TrailersPresenter) {
        self.presenter = presenter
        _uiState = .init(presenter.stateValue)
    }

    public var body: some View {
        TrailersScreen(
            state: uiState.toState(),
            onTrailerSelected: { key in
                presenter.dispatch(action: TrailerSelected(trailerKey: key))
            },
            onVideoError: { message in
                presenter.dispatch(action: VideoPlayerError(errorMessage: message))
            },
            onRetry: {
                presenter.dispatch(action: ReloadTrailers())
            },
            onBack: {
                dismiss()
            }
        )
    }
}

private extension TrailersState {
    func toState() -> TrailersScreen.State {
        TrailersScreen.State(
            title: title,
            screenState: screenState
        )
    }

    var screenState: TrailersScreenState {
        switch self {
        case let state as TrailersContent:
            let trailers = Array(state.trailersList).map { $0.toSwift() }
            return .content(
                selected: trailers.first { $0.key == state.selectedVideoKey },
                trailers: trailers,
                moreTrailersTitle: state.moreTrailersTitle
            )
        case let state as TrailerError:
            return .error(message: state.errorMessage, retryLabel: state.retryLabel)
        default:
            return .loading
        }
    }
}
