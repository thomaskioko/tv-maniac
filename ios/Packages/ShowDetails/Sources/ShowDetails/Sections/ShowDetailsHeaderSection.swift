import Components
import DesignSystem
import Models
import SwiftUI
import TvManiacKit

struct ShowDetailsHeaderSection: View {
    private let presenter: ShowDetailsHeaderPresenter
    @StateValue private var state: ShowDetailsHeaderState

    init(presenter: ShowDetailsHeaderPresenter) {
        self.presenter = presenter
        _state = .init(presenter.stateValue)
    }

    var body: some View {
        ShowInfoView(
            isFollowed: state.isInLibrary,
            canAddToList: state.canAddToList,
            genres: Array(state.genres).map { $0.toSwift() },
            trackLabel: String(\.following),
            stopTrackingLabel: String(\.unfollow),
            addToListLabel: String(\.btn_add_to_list),
            rateLabel: String(\.label_action_rate),
            onAddToLibrary: {
                presenter.dispatch(action: ShowDetailsFollowClicked(isInLibrary: state.isInLibrary))
            },
            onAddToCustomList: {
                presenter.dispatch(action: ShowDetailsOpenShowList())
            },
            onRate: {
                presenter.dispatch(action: ShowRatingClicked())
            }
        )
    }
}
