import SwiftUI
import SwiftUIComponents
import TvManiacKit

struct ProfileTab: View {
    @Environment(ToastManager.self) private var toastManager

    private let presenter: ProfilePresenter
    @StateObject @KotlinStateFlow private var uiState: ProfileState

    init(presenter: ProfilePresenter) {
        self.presenter = presenter
        _uiState = .init(presenter.state)
    }

    var body: some View {
        ProfileScreen(
            title: String(\.profile_title),
            isLoading: uiState.showLoading,
            userProfile: uiState.userProfile.map { profile in
                SwiftProfileInfo(
                    username: profile.username,
                    fullName: profile.fullName,
                    avatarUrl: profile.avatarUrl,
                    backgroundUrl: profile.backgroundUrl,
                    stats: SwiftProfileStats(
                        months: profile.stats.months,
                        days: profile.stats.days,
                        hours: profile.stats.hours,
                        episodesWatched: profile.stats.episodesWatched
                    )
                )
            },
            editButtonLabel: String(\.profile_edit_button),
            statsTitle: String(\.profile_stats_title),
            watchTimeLabel: String(\.profile_watch_time),
            monthsLabel: String(\.profile_time_months),
            daysLabel: String(\.profile_time_days),
            hoursLabel: String(\.profile_time_hours),
            episodesWatchedLabel: String(\.profile_episodes_watched),
            unauthenticatedTitle: String(\.profile_unauthenticated_title),
            footerDescription: String(\.profile_footer_description),
            signInLabel: String(\.profile_sign_in_button),
            featureItems: [
                SwiftFeatureItem(
                    id: "discover",
                    iconName: "magnifyingglass",
                    title: String(\.profile_feature_discover_title),
                    description: String(\.profile_feature_discover_description)
                ),
                SwiftFeatureItem(
                    id: "track",
                    iconName: "tv",
                    title: String(\.profile_feature_track_title),
                    description: String(\.profile_feature_track_description)
                ),
                SwiftFeatureItem(
                    id: "manage",
                    iconName: "rectangle.stack",
                    title: String(\.profile_feature_manage_title),
                    description: String(\.profile_feature_manage_description)
                ),
                SwiftFeatureItem(
                    id: "more",
                    iconName: "sparkles",
                    title: String(\.profile_feature_more_title),
                    description: String(\.profile_feature_more_description)
                ),
            ],
            onSettingsClicked: { presenter.dispatch(action: ProfileActionSettingsClicked()) },
            onLoginClicked: { presenter.dispatch(action: ProfileActionLoginClicked()) }
        )
        .onChange(of: uiState.errorMessage) { _, errorMessage in
            if let errorMessage {
                toastManager.showError(title: "Error", message: errorMessage.message)
            }
        }
        .onChange(of: toastManager.toast) { _, newValue in
            if newValue == nil, let errorMessage = uiState.errorMessage {
                presenter.dispatch(action: ProfileActionMessageShown(id: errorMessage.id))
            }
        }
    }
}
