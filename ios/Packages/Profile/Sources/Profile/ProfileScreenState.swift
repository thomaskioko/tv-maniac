import Components
import DesignSystem
import Models
import SwiftUI
import TvManiac

public extension ProfileScreen {
    struct State: Equatable {
        public let title: String
        public let isLoading: Bool
        public let userProfile: SwiftProfileInfo?
        public let editButtonLabel: String
        public let statsTitle: String
        public let watchTimeLabel: String
        public let monthsLabel: String
        public let daysLabel: String
        public let hoursLabel: String
        public let episodesWatchedLabel: String
        public let showsWatchedLabel: String
        public let listsLabel: String
        public let listsViewLabel: String
        public let userListsTitle: String
        public let viewAllLabel: String
        public let retryLabel: String
        public let userLists: SwiftSectionState<SwiftProfileList>
        public let progressTitle: String
        public let inProgressLabel: String
        public let completedLabel: String
        public let progressEmptyLabel: String
        public let inProgress: SwiftSectionState<SwiftProfileShow>
        public let completed: SwiftSectionState<SwiftProfileShow>
        public let recentlyWatchedTitle: String
        public let recentlyWatched: SwiftSectionState<SwiftProfileRecentShow>
        public let favoritesTitle: String
        public let favorites: SwiftSectionState<SwiftProfileShow>
        public let unauthenticatedTitle: String
        public let authTitle: String
        public let authDescription: String
        public let isAuthenticated: Bool
        public let featureItems: [SwiftFeatureItem]
        public let authProviders: [SwiftAuthProvider]

        public init(
            title: String,
            isLoading: Bool,
            userProfile: SwiftProfileInfo?,
            editButtonLabel: String,
            statsTitle: String,
            watchTimeLabel: String,
            monthsLabel: String,
            daysLabel: String,
            hoursLabel: String,
            episodesWatchedLabel: String,
            showsWatchedLabel: String,
            listsLabel: String,
            listsViewLabel: String,
            userListsTitle: String = "",
            viewAllLabel: String = "",
            retryLabel: String = "",
            userLists: SwiftSectionState<SwiftProfileList> = .empty,
            progressTitle: String = "",
            inProgressLabel: String = "",
            completedLabel: String = "",
            progressEmptyLabel: String = "",
            inProgress: SwiftSectionState<SwiftProfileShow> = .empty,
            completed: SwiftSectionState<SwiftProfileShow> = .empty,
            recentlyWatchedTitle: String = "",
            recentlyWatched: SwiftSectionState<SwiftProfileRecentShow> = .empty,
            favoritesTitle: String = "",
            favorites: SwiftSectionState<SwiftProfileShow> = .empty,
            unauthenticatedTitle: String,
            authTitle: String,
            authDescription: String,
            isAuthenticated: Bool,
            featureItems: [SwiftFeatureItem],
            authProviders: [SwiftAuthProvider] = []
        ) {
            self.title = title
            self.isLoading = isLoading
            self.userProfile = userProfile
            self.editButtonLabel = editButtonLabel
            self.statsTitle = statsTitle
            self.watchTimeLabel = watchTimeLabel
            self.monthsLabel = monthsLabel
            self.daysLabel = daysLabel
            self.hoursLabel = hoursLabel
            self.episodesWatchedLabel = episodesWatchedLabel
            self.showsWatchedLabel = showsWatchedLabel
            self.listsLabel = listsLabel
            self.listsViewLabel = listsViewLabel
            self.userListsTitle = userListsTitle
            self.viewAllLabel = viewAllLabel
            self.retryLabel = retryLabel
            self.userLists = userLists
            self.progressTitle = progressTitle
            self.inProgressLabel = inProgressLabel
            self.completedLabel = completedLabel
            self.progressEmptyLabel = progressEmptyLabel
            self.inProgress = inProgress
            self.completed = completed
            self.recentlyWatchedTitle = recentlyWatchedTitle
            self.recentlyWatched = recentlyWatched
            self.favoritesTitle = favoritesTitle
            self.favorites = favorites
            self.unauthenticatedTitle = unauthenticatedTitle
            self.authTitle = authTitle
            self.authDescription = authDescription
            self.isAuthenticated = isAuthenticated
            self.featureItems = featureItems
            self.authProviders = authProviders
        }
    }
}
