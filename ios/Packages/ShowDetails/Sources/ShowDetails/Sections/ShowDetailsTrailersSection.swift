import Components
import DesignSystem
import Models
import SwiftUI
import TvManiacKit

struct ShowDetailsTrailersSection: View {
    private let presenter: ShowDetailsTrailersPresenter
    @StateValue private var state: ShowDetailsTrailersState
    @State private var toast: Toast?

    init(presenter: ShowDetailsTrailersPresenter) {
        self.presenter = presenter
        _state = .init(presenter.stateValue)
    }

    var body: some View {
        let trailers = Array(state.trailersList).map { $0.toSwift() }
        TrailerListView(
            title: state.title,
            trailers: trailers,
            openInYouTube: state.hasWebViewInstalled,
            onMoreClicked: {
                if let id = trailers.first?.showId {
                    presenter.dispatch(action: ShowDetailsWatchTrailerClicked(id: id))
                }
            },
            onError: { error in
                toast = Toast(
                    type: .error,
                    title: "Error",
                    message: "Failed to play video: \(error.localizedDescription)",
                    duration: 3.5
                )
            }
        )
        .toastView(toast: $toast)
    }
}
