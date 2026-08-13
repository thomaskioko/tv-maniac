import Components
import DesignSystem
import SwiftUI
import TvManiacKit

public struct EpisodeDetailSheetView: View {
    private let presenter: EpisodeSheetPresenter
    @StateValue private var state: EpisodeDetailSheetState
    @Environment(\.hapticFeedbackEnabled) private var hapticFeedbackEnabled
    @State private var selectedDetent: PresentationDetent = .large
    @State private var toast: Toast?

    public init(presenter: EpisodeSheetPresenter) {
        self.presenter = presenter
        _state = .init(presenter.stateValue)
    }

    public var body: some View {
        Group {
            if state.isLoading {
                EpisodeDetailSheetLoadingUi()
            } else {
                EpisodeDetailSheetContent(
                    episode: EpisodeDetailSheetInfo(
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
                        voteCount: state.voteCount as? Int64,
                        isWatched: state.isWatched
                    )
                ) {
                    let actions = Array(state.availableActions)
                    if let markWatchedAction = actions.first {
                        actionView(for: markWatchedAction)
                    }
                    SheetActionItem(
                        icon: (state.userRating as? Int) != nil ? "star.fill" : "star",
                        label: rateActionLabel,
                        action: { presenter.dispatch(action: EpisodeSheetActionRatingClicked()) }
                    )
                    ForEach(Array(actions.dropFirst()), id: \.self) { action in
                        actionView(for: action)
                    }
                }
            }
        }
        .presentationDetents([.medium, .large], selection: $selectedDetent)
        .presentationDragIndicator(.visible)
        .presentationCornerRadius(16)
        .appTheme()
        .toastView(toast: $toast)
        .onChange(of: state.message) { _, newValue in
            if let message = newValue {
                toast = Toast(type: .error, title: String(\.label_error), message: message.message)
                presenter.dispatch(action: EpisodeSheetActionMessageShown(id: message.id))
            }
        }
        .alert(
            state.removeWatchConfirmation?.title ?? "",
            isPresented: Binding(
                get: { state.removeWatchConfirmation != nil },
                set: { isPresented in
                    if !isPresented { presenter.dispatch(action: EpisodeSheetActionRemoveWatchDismissed()) }
                }
            )
        ) {
            if let confirmation = state.removeWatchConfirmation {
                Button(confirmation.confirmLabel, role: .destructive) {
                    presenter.dispatch(action: EpisodeSheetActionRemoveWatchConfirmed())
                }
                Button(confirmation.dismissLabel, role: .cancel) {
                    presenter.dispatch(action: EpisodeSheetActionRemoveWatchDismissed())
                }
            }
        } message: {
            Text(state.removeWatchConfirmation?.message ?? "")
        }
    }

    private var rateActionLabel: String {
        String(\.label_action_rate_episode)
    }

    @ViewBuilder
    private func actionView(for action: EpisodeSheetActionUi) -> some View {
        switch action.item {
        case .markWatched:
            SheetActionItem(
                icon: state.isWatched ? "checkmark.circle.fill" : "checkmark.circle",
                label: action.label,
                isEnabled: !state.isTogglingWatched,
                showProgress: state.isTogglingWatched,
                action: {
                    Haptics.impact(isEnabled: hapticFeedbackEnabled)
                    presenter.dispatch(action: EpisodeSheetActionMarkWatched())
                }
            )
        case .markUnwatched:
            SheetActionItem(
                icon: "xmark.circle",
                label: action.label,
                isEnabled: !state.isTogglingWatched,
                action: { presenter.dispatch(action: EpisodeSheetActionMarkUnwatched()) }
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
