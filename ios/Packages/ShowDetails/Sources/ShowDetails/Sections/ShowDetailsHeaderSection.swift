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
            isInList: state.isInList,
            genres: Array(state.genres).map { $0.toSwift() },
            trackLabel: String(\.following),
            stopTrackingLabel: String(\.unfollow),
            listActionLabel: state.listActionLabel,
            rateLabel: String(\.label_action_rate),
            userRating: state.userRating as? Int,
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
