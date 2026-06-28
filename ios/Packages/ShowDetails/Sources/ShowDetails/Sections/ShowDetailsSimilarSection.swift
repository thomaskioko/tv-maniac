import Components
import DesignSystem
import Models
import SwiftUI
import TvManiacKit

struct ShowDetailsSimilarSection: View {
    private let presenter: ShowDetailsSimilarPresenter
    @StateValue private var state: ShowDetailsSimilarState

    init(presenter: ShowDetailsSimilarPresenter) {
        self.presenter = presenter
        _state = .init(presenter.stateValue)
    }

    var body: some View {
        HorizontalShowContentView(
            title: String(\.title_similar),
            cardStyle: .backdrop,
            items: Array(state.similarShows).map { $0.toSwift() },
            onClick: { id in
                presenter.dispatch(action: ShowDetailsSimilarShowClicked(showId: id))
            }
        )
    }
}
