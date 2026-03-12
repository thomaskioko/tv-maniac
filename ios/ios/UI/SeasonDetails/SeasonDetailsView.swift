import SwiftUI
import SwiftUIComponents
import TvManiacKit

struct SeasonDetailsView: View {
    private let presenter: SeasonDetailsPresenter

    @StateObject @KotlinStateFlow private var uiState: SeasonDetailsModel
    @State private var showGallery = false
    @State private var toast: Toast?
    @State private var showMarkPreviousAlert = false
    @State private var showUnwatchedConfirmAlert = false
    @State private var showMarkPreviousSeasonsAlert = false
    @State private var showSeasonUnwatchAlert = false

    init(presenter: SeasonDetailsPresenter) {
        self.presenter = presenter
        _uiState = .init(presenter.state)
    }

    var body: some View {
        SeasonDetailsScreen(
            seasonName: uiState.seasonName,
            imageUrl: uiState.imageUrl,
            seasonOverview: uiState.seasonOverview,
            episodeCount: uiState.episodeCount,
            watchProgress: uiState.watchProgress,
            expandEpisodeItems: uiState.expandEpisodeItems,
            isSeasonWatched: uiState.isSeasonWatched,
            isRefreshing: uiState.isRefreshing,
            showError: uiState.showError,
            seasonImages: uiState.seasonImages.map { $0.toSwift() },
            episodes: uiState.episodeDetailsList.map { $0.toSwift() },
            casts: uiState.seasonCast.map { cast in
                SwiftCast(
                    castId: cast.id,
                    name: cast.name,
                    characterName: cast.characterName,
                    profileUrl: cast.profileUrl
                )
            },
            errorTitle: String(\.generic_error_message),
            errorRetryText: String(\.button_error_retry),
            overviewTitle: String(\.title_season_overview),
            episodesTitle: String(\.title_episodes),
            seasonImagesCountFormat: { count in String(\.season_images_count, quantity: count) },
            dayLabelFormat: { count in String(\.day_label, quantity: count) },
            toast: $toast,
            showGallery: $showGallery,
            onBack: { presenter.dispatch(action: SeasonDetailsBackClicked()) },
            onRetry: { presenter.dispatch(action: ReloadSeasonDetails()) },
            onGalleryTap: {
                presenter.dispatch(action: ShowGallery())
                showGallery.toggle()
            },
            onEpisodeHeaderClicked: { presenter.dispatch(action: OnEpisodeHeaderClicked()) },
            onWatchedStateClicked: { presenter.dispatch(action: ToggleSeasonWatched()) },
            onEpisodeWatchToggle: { episode in
                presenter.dispatch(action: ToggleEpisodeWatched(episodeId: episode.episodeId))
            }
        )
        .onChange(of: uiState.message) { _, newValue in
            if let message = newValue {
                toast = Toast(type: .error, title: "Error", message: message.message)
                presenter.dispatch(action: SeasonDetailsMessageShown(id: message.id))
            }
        }
        .onChange(of: uiState.isDialogVisible) { _, _ in
            let dialogState = uiState.dialogState
            showMarkPreviousAlert = dialogState is SeasonDialogStateMarkPreviousEpisodesConfirmation
            showUnwatchedConfirmAlert = dialogState is SeasonDialogStateUnwatchEpisodeConfirmation
            showMarkPreviousSeasonsAlert = dialogState is SeasonDialogStateMarkPreviousSeasonsConfirmation
            showSeasonUnwatchAlert = dialogState is SeasonDialogStateUnwatchSeasonConfirmation
        }
        .onChange(of: uiState.isGalleryVisible) { _, isVisible in
            showGallery = isVisible
        }
        .alert(String(\.dialog_title_unwatched), isPresented: $showSeasonUnwatchAlert) {
            Button(String(\.dialog_button_yes)) {
                presenter.dispatch(action: ConfirmDialogAction())
            }
            Button(String(\.dialog_button_no), role: .cancel) {
                presenter.dispatch(action: DismissDialog())
            }
        } message: {
            Text(String(\.dialog_message_unwatched))
        }
        .alert(String(\.dialog_title_mark_previous), isPresented: $showMarkPreviousAlert) {
            Button(String(\.dialog_button_mark_all)) {
                presenter.dispatch(action: ConfirmDialogAction())
            }
            Button(String(\.dialog_button_just_this), role: .cancel) {
                presenter.dispatch(action: SecondaryDialogAction())
            }
        } message: {
            Text(String(\.dialog_message_mark_previous))
        }
        .alert(String(\.dialog_title_episode_unwatched), isPresented: $showUnwatchedConfirmAlert) {
            Button(String(\.dialog_button_yes)) {
                presenter.dispatch(action: ConfirmDialogAction())
            }
            Button(String(\.dialog_button_no), role: .cancel) {
                presenter.dispatch(action: DismissDialog())
            }
        } message: {
            Text(String(\.dialog_message_episode_unwatched))
        }
        .alert(String(\.dialog_title_mark_previous_seasons), isPresented: $showMarkPreviousSeasonsAlert) {
            Button(String(\.dialog_button_mark_all_seasons)) {
                presenter.dispatch(action: ConfirmDialogAction())
            }
            Button(String(\.dialog_button_just_this_season), role: .cancel) {
                presenter.dispatch(action: SecondaryDialogAction())
            }
        } message: {
            Text(String(\.dialog_message_mark_previous_seasons))
        }
    }
}
