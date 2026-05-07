import SwiftUI
import SwiftUIComponents
import TvManiacKit

struct EpisodeDetailSheetView: View {
    private let presenter: EpisodeSheetPresenter
    @StateValue private var state: EpisodeDetailSheetState
    @State private var selectedDetent: PresentationDetent = .large
    @State private var toast: Toast?

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
            .toastView(toast: $toast)
            .onChange(of: state.message) { _, newValue in
                if let message = newValue {
                    toast = Toast(type: .error, title: "Error", message: message.message)
                    presenter.dispatch(action: EpisodeSheetActionMessageShown(id: message.id))
                }
            }
        }
    }

    @ViewBuilder
    private func actionView(for action: EpisodeSheetActionUi) -> some View {
        switch action.item {
        case .toggleWatched:
            SheetActionItem(
                icon: state.isWatched ? "checkmark.circle.fill" : "checkmark.circle",
                label: action.label,
                action: { presenter.dispatch(action: EpisodeSheetActionToggleWatched()) }
            )
        case .openShow:
            SheetActionItem(
                icon: "tv",
                label: action.label,
                action: { presenter.dispatch(action: EpisodeSheetActionOpenShow()) }
            )
        case .openSeason:
            SheetActionItem(
                icon: "list.bullet",
                label: action.label,
                action: { presenter.dispatch(action: EpisodeSheetActionOpenSeason()) }
            )
        case .unfollow:
            SheetActionItem(
                icon: "minus.circle",
                label: action.label,
                action: { presenter.dispatch(action: EpisodeSheetActionUnfollow()) }
            )
        default:
            EmptyView()
        }
    }
}
