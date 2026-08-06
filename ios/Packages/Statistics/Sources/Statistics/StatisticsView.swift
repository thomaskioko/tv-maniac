import Components
import SwiftUI
import TvManiac
import TvManiacKit

public struct StatisticsView: View {
    @Environment(ToastManager.self) private var toastManager

    private let presenter: StatisticsPresenter
    @StateValue private var uiState: StatisticsState

    public init(presenter: StatisticsPresenter) {
        self.presenter = presenter
        _uiState = .init(presenter.stateValue)
    }

    public var body: some View {
        StatisticsScreen(
            state: uiState.toState(),
            backButtonAccessibilityLabel: String(\.cd_back),
            onBack: { presenter.dispatch(action: StatisticsActionBackClicked()) },
            onUpgradeClicked: { presenter.dispatch(action: StatisticsActionUpgradeClicked()) },
            onShowClicked: { showId in presenter.dispatch(action: StatisticsActionShowClicked(showId: showId)) }
        )
        .onChange(of: uiState.message) { _, message in
            if let message {
                toastManager.showError(title: "Error", message: message.message)
            }
        }
        .onChange(of: toastManager.toast) { _, newValue in
            if newValue == nil, let message = uiState.message {
                presenter.dispatch(action: StatisticsActionMessageShown(id: message.id))
            }
        }
        .screenTag(StatisticsTestTags.shared.SCREEN_TEST_TAG)
    }
}
