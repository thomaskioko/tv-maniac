import Components
import DesignSystem
import SwiftUI
import TvManiacKit

public struct RatingSheetView: View {
    private let presenter: RatingSheetPresenter
    @StateValue private var state: RatingSheetState

    public init(presenter: RatingSheetPresenter) {
        self.presenter = presenter
        _state = .init(presenter.stateValue)
    }

    public var body: some View {
        RatingSheetContent(
            title: state.title,
            removeLabel: state.removeRatingLabel,
            userRating: state.userRating as? Int,
            onRatingSelected: { rating in
                presenter.dispatch(action: RatingSheetActionRatingSelected(rating: Int32(rating)))
            },
            onRemove: {
                presenter.dispatch(action: RatingSheetActionRatingCleared())
            }
        )
        .presentationDetents([.medium])
        .presentationDragIndicator(.hidden)
        .presentationCornerRadius(16)
        .appTheme()
    }
}
