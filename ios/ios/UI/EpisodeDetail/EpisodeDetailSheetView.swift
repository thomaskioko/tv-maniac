import SwiftUI
import SwiftUIComponents
import TvManiacKit

struct EpisodeDetailSheetView: View {
    private let presenter: EpisodeSheetPresenter
    @StateValue private var state: EpisodeDetailSheetState
    @State private var selectedDetent: PresentationDetent = .large

    init(presenter: EpisodeSheetPresenter) {
        self.presenter = presenter
        _state = .init(presenter.stateValue)
    }

    var body: some View {
        if !state.isLoading {
            EpisodeDetailSheetContent(
                episode: EpisodeDetailInfo(
                    title: state.episodeTitle,
                    imageUrl: state.imageUrl,
                    episodeInfo: {
                        var text = state.seasonEpisodeNumber
                        if !state.showName.isEmpty {
                            text += " \u{2022} \(state.showName)"
                        }
                        return text
                    }(),
                    overview: state.overview,
                    rating: state.rating as? Double,
                    voteCount: state.voteCount as? Int64
                )
            ) {
                ForEach(Array(state.availableActions), id: \.self) { action in
                    actionView(for: action)
                }
            }
            .presentationDetents([.medium, .large], selection: $selectedDetent)
            .presentationDragIndicator(.visible)
            .presentationCornerRadius(16)
            .appTheme()
        }
    }

    @ViewBuilder
    private func actionView(for action: EpisodeSheetActionItem) -> some View {
        switch action {
        case .toggleWatched:
            SheetActionItem(
                icon: state.isWatched ? "checkmark.circle.fill" : "checkmark.circle",
                label: state.isWatched ? String(\.dialog_title_unwatched) : String(\.menu_mark_watched),
                action: { presenter.dispatch(action: EpisodeSheetActionToggleWatched()) }
            )
        case .openShow:
            SheetActionItem(
                icon: "tv",
                label: String(\.menu_open_show),
                action: { presenter.dispatch(action: EpisodeSheetActionOpenShow()) }
            )
        case .openSeason:
            SheetActionItem(
                icon: "list.bullet",
                label: String(\.menu_open_season),
                action: { presenter.dispatch(action: EpisodeSheetActionOpenSeason()) }
            )
        case .unfollow:
            SheetActionItem(
                icon: "minus.circle",
                label: String(\.menu_unfollow_show),
                action: { presenter.dispatch(action: EpisodeSheetActionUnfollow()) }
            )
        default:
            EmptyView()
        }
    }
}
