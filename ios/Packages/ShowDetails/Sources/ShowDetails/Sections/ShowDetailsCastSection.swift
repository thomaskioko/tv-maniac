import Components
import DesignSystem
import Models
import SwiftUI
import TvManiacKit

struct ShowDetailsCastSection: View {
    private let presenter: ShowDetailsCastPresenter
    @StateValue private var state: ShowDetailsCastState

    init(presenter: ShowDetailsCastPresenter) {
        self.presenter = presenter
        _state = .init(presenter.stateValue)
    }

    var body: some View {
        CastListView(
            casts: Array(state.castsList).map { $0.toSwift() }
        )
    }
}
