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
            moreLabel: String(\.label_action_more),
            rateLabel: String(\.label_action_rate),
            watchAgainLabel: String(\.label_action_watch_again),
            canWatchAgain: state.canWatchAgain,
            userRating: state.userRating as? Int,
            onAddToLibrary: {
                presenter.dispatch(action: ShowDetailsFollowClicked(isInLibrary: state.isInLibrary))
            },
            onAddToCustomList: {
                presenter.dispatch(action: ShowDetailsOpenShowList())
            },
            onRate: {
                presenter.dispatch(action: ShowRatingClicked())
            },
            onWatchAgain: {
                presenter.dispatch(action: WatchAgainClicked())
            }
        )
        .alert(
            String(\.label_watch_again_confirm_title),
            isPresented: Binding(
                get: { state.showWatchAgainConfirmation },
                set: { isPresented in
                    if !isPresented {
                        presenter.dispatch(action: WatchAgainDismissed())
                    }
                }
            )
        ) {
            Button(String(\.label_action_watch_again)) {
                presenter.dispatch(action: WatchAgainConfirmed())
            }
            Button(String(\.dialog_button_no), role: .cancel) {
                presenter.dispatch(action: WatchAgainDismissed())
            }
        } message: {
            Text(String(\.label_watch_again_confirm_message, parameter: state.title))
        }
    }
}
