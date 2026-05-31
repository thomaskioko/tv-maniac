import DesignSystem
import SwiftUI
import SwiftUIComponents
import TvManiacKit

struct ProfileTab: View {
    @Environment(ToastManager.self) private var toastManager

    private let presenter: ProfilePresenter
    @StateValue private var uiState: ProfileState

    init(presenter: ProfilePresenter) {
        self.presenter = presenter
        _uiState = .init(presenter.stateValue)
    }

    var body: some View {
        ProfileScreen(
            state: uiState.toState(),
            onSettingsClicked: { presenter.dispatch(action: ProfileActionSettingsClicked()) },
            onLoginClicked: { presenter.dispatch(action: ProfileActionLoginClicked()) },
            onViewListsClicked: { presenter.dispatch(action: ProfileActionViewListsClicked()) },
            onRetryLists: { presenter.dispatch(action: ProfileActionRefreshProfile()) },
            onShowClicked: { traktId in presenter.dispatch(action: ProfileActionShowClicked(traktId: traktId)) },
            onRetryProgress: { presenter.dispatch(action: ProfileActionRefreshProfile()) }
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

private extension ProfileState {
    func toState() -> ProfileScreen.State {
        ProfileScreen.State(
            title: labels.title,
            isLoading: showLoading,
            userProfile: userProfile.map { profile in
                SwiftProfileInfo(
                    username: profile.username,
                    fullName: profile.fullName,
                    avatarUrl: profile.avatarUrl,
                    backgroundUrl: profile.backgroundUrl,
                    stats: SwiftProfileStats(
                        showsWatched: profile.stats.showsWatched,
                        episodesWatched: profile.stats.episodesWatched,
                        months: profile.stats.months,
                        days: profile.stats.days,
                        hours: profile.stats.hours,
                        listCount: listCount
                    )
                )
            },
            editButtonLabel: labels.editButton,
            statsTitle: labels.statsTitle,
            watchTimeLabel: labels.watchTime,
            monthsLabel: labels.monthsShort,
            daysLabel: labels.daysShort,
            hoursLabel: labels.hoursShort,
            episodesWatchedLabel: labels.episodesWatched,
            showsWatchedLabel: labels.showsWatched,
            listsLabel: labels.lists,
            listsViewLabel: labels.viewButton,
            userListsTitle: labels.userListsTitle,
            viewAllLabel: labels.viewAllButton,
            retryLabel: labels.retry,
            userLists: userLists.toSwiftSectionState(),
            progressTitle: labels.progressTitle,
            inProgressLabel: labels.inProgressFilter,
            completedLabel: labels.completedFilter,
            progressEmptyLabel: labels.progressEmpty,
            inProgress: inProgress.toSwiftShowSectionState(),
            completed: completed.toSwiftShowSectionState(),
            recentlyWatchedTitle: labels.recentlyWatchedTitle,
            recentlyWatched: recentlyWatched.toSwiftRecentSectionState(),
            favoritesTitle: labels.favoritesTitle,
            favorites: favorites.toSwiftShowSectionState(),
            unauthenticatedTitle: labels.unauthenticatedTitle,
            footerDescription: labels.footerDescription,
            signInLabel: labels.signInButton,
            featureItems: [
                SwiftFeatureItem(
                    id: "discover",
                    iconName: "magnifyingglass",
                    title: labels.featureDiscoverTitle,
                    description: labels.featureDiscoverDescription
                ),
                SwiftFeatureItem(
                    id: "track",
                    iconName: "tv",
                    title: labels.featureTrackTitle,
                    description: labels.featureTrackDescription
                ),
                SwiftFeatureItem(
                    id: "manage",
                    iconName: "rectangle.stack",
                    title: labels.featureManageTitle,
                    description: labels.featureManageDescription
                ),
                SwiftFeatureItem(
                    id: "more",
                    iconName: "sparkles",
                    title: labels.featureMoreTitle,
                    description: labels.featureMoreDescription
                ),
            ]
        )
    }
}

private extension SectionState {
    func toSwiftSectionState() -> SwiftSectionState<SwiftProfileList> {
        switch self {
        case is SectionStateLoading:
            return .loading
        case is SectionStateEmpty:
            return .empty
        case let error as SectionStateError:
            return .error(error.message.message)
        case let content as SectionStateContent<ProfileListItem>:
            let items = content.items.compactMap { $0 as? ProfileListItem }.map { item in
                SwiftProfileList(
                    id: item.id,
                    name: item.name,
                    itemCountLabel: item.itemCountLabel,
                    posterUrls: item.posterUrls
                )
            }
            return .content(items)
        default:
            return .empty
        }
    }

    func toSwiftShowSectionState() -> SwiftSectionState<SwiftProfileShow> {
        switch self {
        case is SectionStateLoading:
            return .loading
        case is SectionStateEmpty:
            return .empty
        case let error as SectionStateError:
            return .error(error.message.message)
        case let content as SectionStateContent<ProfileShowItem>:
            let items = content.items.compactMap { $0 as? ProfileShowItem }.map { item in
                SwiftProfileShow(
                    id: item.traktId,
                    title: item.title,
                    posterUrl: item.posterUrl
                )
            }
            return .content(items)
        default:
            return .empty
        }
    }

    func toSwiftRecentSectionState() -> SwiftSectionState<SwiftProfileRecentShow> {
        switch self {
        case is SectionStateLoading:
            return .loading
        case is SectionStateEmpty:
            return .empty
        case let error as SectionStateError:
            return .error(error.message.message)
        case let content as SectionStateContent<ProfileRecentItem>:
            let items = content.items.compactMap { $0 as? ProfileRecentItem }.map { item in
                SwiftProfileRecentShow(
                    traktId: item.traktId,
                    title: item.title,
                    posterUrl: item.posterUrl,
                    episodeLabel: item.episodeLabel
                )
            }
            return .content(items)
        default:
            return .empty
        }
    }
}
