import Components
import DesignSystem
import Models
import SwiftUI
import TvManiacKit

struct DiscoverStartWatchingSection: View {
    private let presenter: DiscoverStartWatchingPresenter
    @StateValue private var state: DiscoverStartWatchingState

    init(presenter: DiscoverStartWatchingPresenter) {
        self.presenter = presenter
        _state = .init(presenter.stateValue)
    }

    var body: some View {
        DiscoverStartWatchingContent(
            title: state.startWatchingTitle,
            shows: state.startWatchingShowsSwift,
            onShowClicked: { id in
                presenter.dispatch(action: StartWatchingItemClicked(showId: id))
            },
            onMoreClicked: {
                presenter.dispatch(action: StartWatchingMoreClicked())
            }
        )
    }
}

struct DiscoverStartWatchingContent: View {
    let title: String
    let shows: [SwiftShow]
    let onShowClicked: (Int64) -> Void
    let onMoreClicked: () -> Void

    var body: some View {
        HorizontalShowContentView(
            title: title,
            chevronStyle: .chevronOnly,
            cardStyle: .poster,
            libraryImageOverlay: "clock.fill",
            items: shows,
            onClick: onShowClicked,
            onMoreClicked: onMoreClicked
        )
    }
}

#Preview("Start Watching - Empty") {
    DiscoverStartWatchingContent(
        title: "Start Watching",
        shows: [],
        onShowClicked: { _ in },
        onMoreClicked: {}
    )
    .appPreview()
}

#Preview("Start Watching - Content") {
    DiscoverStartWatchingContent(
        title: "Start Watching",
        shows: [
            SwiftShow(showId: 1, title: "Breaking Bad", posterUrl: nil, inLibrary: true),
            SwiftShow(showId: 2, title: "Game of Thrones", posterUrl: nil, inLibrary: true),
            SwiftShow(showId: 3, title: "The Wire", posterUrl: nil, inLibrary: true),
        ],
        onShowClicked: { _ in },
        onMoreClicked: {}
    )
    .appPreview()
}
