import Components
import DesignSystem
import SwiftUI
import TvManiacKit

public struct RatingSheetView: View {
    private let presenter: RatingSheetPresenter
    @StateValue private var state: RatingSheetState
    @ObservedObject private var store = SettingsAppStorage.shared
    @State private var sheetHeight: CGFloat = 200

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
        .background {
            GeometryReader { proxy in
                Color.clear.onChange(of: proxy.size.height, initial: true) { _, height in
                    sheetHeight = height + proxy.safeAreaInsets.bottom
                }
            }
        }
        .presentationDetents([.height(sheetHeight)])
        .presentationDragIndicator(.hidden)
        .presentationBackground(store.appTheme.designSystemTheme.colors.surface)
        .presentationCornerRadius(16)
        .appTheme()
    }
}
