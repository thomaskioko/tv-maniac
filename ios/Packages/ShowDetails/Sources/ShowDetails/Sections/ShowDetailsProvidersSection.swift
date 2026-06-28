import Components
import DesignSystem
import Models
import SwiftUI
import TvManiacKit

struct ShowDetailsProvidersSection: View {
    private let presenter: ShowDetailsProvidersPresenter
    @StateValue private var state: ShowDetailsProvidersState

    init(presenter: ShowDetailsProvidersPresenter) {
        self.presenter = presenter
        _state = .init(presenter.stateValue)
    }

    var body: some View {
        ProviderListView(
            items: Array(state.providers).map { $0.toSwift() }
        )
    }
}
