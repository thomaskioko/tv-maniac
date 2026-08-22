import Components
import DesignSystem
import SwiftUI
import TvManiacKit

public struct WatchDateSelectionView: View {
    private let presenter: WatchDateSelectionPresenter
    @StateValue private var state: WatchDateSelectionState
    @ObservedObject private var store = SettingsAppStorage.shared
    @State private var sheetHeight: CGFloat = 320

    public init(presenter: WatchDateSelectionPresenter) {
        self.presenter = presenter
        _state = .init(presenter.stateValue)
    }

    public var body: some View {
        WatchDateSelectionContent(
            title: state.title,
            currentWatchedAtLabel: state.currentWatchedAtLabel,
            justNowLabel: state.justNowLabel,
            releaseDateLabel: state.releaseDateLabel,
            otherDateLabel: state.otherDateLabel,
            unknownDateLabel: state.unknownDateLabel,
            confirmLabel: String(\.label_ok),
            cancelLabel: String(\.label_cancel),
            isReleaseDateEnabled: state.isReleaseDateEnabled,
            onJustNow: {
                presenter.dispatch(action: WatchDateSelectionActionJustNowSelected())
            },
            onReleaseDate: {
                presenter.dispatch(action: WatchDateSelectionActionReleaseDateSelected())
            },
            onOtherDate: { date in
                presenter.dispatch(action: otherDateAction(for: date))
            },
            onUnknownDate: {
                presenter.dispatch(action: WatchDateSelectionActionUnknownDateSelected())
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

    private func otherDateAction(for date: Date) -> WatchDateSelectionActionOtherDateSelected {
        let parts = Calendar.current.dateComponents([.year, .month, .day, .hour, .minute], from: date)
        return WatchDateSelectionActionOtherDateSelected(
            date: Kotlinx_datetimeLocalDate(
                year: Int32(parts.year ?? 0),
                month: Int32(parts.month ?? 1),
                day: Int32(parts.day ?? 1)
            ),
            time: Kotlinx_datetimeLocalTime(
                hour: Int32(parts.hour ?? 0),
                minute: Int32(parts.minute ?? 0),
                second: 0,
                nanosecond: 0
            )
        )
    }
}
