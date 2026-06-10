import Components
import DesignSystem
import Models
import Profile
import SnapshotTestingLib
import SwiftUI
import XCTest

class ProfileScreenTest: SnapshotTestCase {
    private let sampleFeatureItems: [SwiftFeatureItem] = [
        SwiftFeatureItem(
            id: "discover",
            iconName: "magnifyingglass",
            title: "Discover Shows",
            description: "Find new shows to watch"
        ),
        SwiftFeatureItem(
            id: "track",
            iconName: "tv",
            title: "Track Progress",
            description: "Keep track of what you've watched"
        ),
        SwiftFeatureItem(
            id: "manage",
            iconName: "rectangle.stack",
            title: "Manage Library",
            description: "Organize your shows"
        ),
        SwiftFeatureItem(
            id: "more",
            iconName: "sparkles",
            title: "And More",
            description: "Get personalized recommendations"
        ),
    ]

    func test_ProfileScreen_Loading() {
        ProfileScreen(
            state: ProfileScreen.State(
                title: "Profile",
                isLoading: true,
                userProfile: nil,
                editButtonLabel: "Edit Profile",
                statsTitle: "Stats",
                watchTimeLabel: "Watch Time",
                monthsLabel: "M",
                daysLabel: "D",
                hoursLabel: "H",
                episodesWatchedLabel: "Episodes Watched",
                showsWatchedLabel: "Shows Watched",
                listsLabel: "Lists",
                listsViewLabel: "View",
                unauthenticatedTitle: "Track your shows",
                footerDescription: "Sign in to sync your data.",
                isAuthenticated: false,
                featureItems: sampleFeatureItems
            ),
            onSettingsClicked: {},
            onProviderSelected: { _ in }
        )
        .appPreview()
        .assertSnapshot(layout: .defaultDevice, testName: "ProfileScreen_Loading")
    }

    func test_ProfileScreen_Unauthenticated() {
        ProfileScreen(
            state: ProfileScreen.State(
                title: "Profile",
                isLoading: false,
                userProfile: nil,
                editButtonLabel: "Edit Profile",
                statsTitle: "Stats",
                watchTimeLabel: "Watch Time",
                monthsLabel: "M",
                daysLabel: "D",
                hoursLabel: "H",
                episodesWatchedLabel: "Episodes Watched",
                showsWatchedLabel: "Shows Watched",
                listsLabel: "Lists",
                listsViewLabel: "View",
                unauthenticatedTitle: "Track your shows",
                footerDescription: "Sign in to sync your data across devices.",
                isAuthenticated: false,
                featureItems: sampleFeatureItems
            ),
            onSettingsClicked: {},
            onProviderSelected: { _ in }
        )
        .appPreview()
        .assertSnapshot(layout: .defaultDevice, testName: "ProfileScreen_Unauthenticated")
    }

    private let sampleLists: [SwiftProfileList] = [
        SwiftProfileList(id: 1, name: "Watchlist", itemCountLabel: "24 shows", posterUrls: ["a", "b", "c", "d"]),
        SwiftProfileList(id: 2, name: "Favorites", itemCountLabel: "2 shows", posterUrls: ["e", "f"]),
        SwiftProfileList(id: 3, name: "New List", itemCountLabel: "3 shows", posterUrls: []),
    ]

    private let manyLists: [SwiftProfileList] = (1 ... 5).map { index in
        SwiftProfileList(
            id: Int64(index),
            name: "List \(index)",
            itemCountLabel: "\(index) shows",
            posterUrls: ["a", "b", "c", "d"]
        )
    }

    private func authenticatedProfile() -> SwiftProfileInfo {
        SwiftProfileInfo(
            username: "tvmaniac_user",
            fullName: "John Doe",
            avatarUrl: nil,
            backgroundUrl: nil,
            stats: SwiftProfileStats(
                showsWatched: "87",
                episodesWatched: "1,250",
                months: 2,
                days: 15,
                hours: 8,
                listCount: 12
            )
        )
    }

    private let sampleShows: [SwiftProfileShow] = [
        SwiftProfileShow(id: 1, title: "Breaking Bad", posterUrl: nil),
        SwiftProfileShow(id: 2, title: "Game of Thrones", posterUrl: nil),
        SwiftProfileShow(id: 3, title: "Stranger Things", posterUrl: nil),
    ]

    private let sampleRecentShows: [SwiftProfileRecentShow] = [
        SwiftProfileRecentShow(showId: 1, title: "Breaking Bad", posterUrl: nil, episodeLabel: "S5E14"),
        SwiftProfileRecentShow(showId: 2, title: "Game of Thrones", posterUrl: nil, episodeLabel: "S8E3"),
        SwiftProfileRecentShow(showId: 3, title: "Stranger Things", posterUrl: nil, episodeLabel: "S4E9"),
    ]

    private func authenticatedState(userLists: SwiftSectionState<SwiftProfileList>) -> ProfileScreen.State {
        ProfileScreen.State(
            title: "Profile",
            isLoading: false,
            userProfile: authenticatedProfile(),
            editButtonLabel: "Edit Profile",
            statsTitle: "Stats",
            watchTimeLabel: "Watch Time",
            monthsLabel: "M",
            daysLabel: "D",
            hoursLabel: "H",
            episodesWatchedLabel: "Episodes Watched",
            showsWatchedLabel: "Shows Watched",
            listsLabel: "Lists",
            listsViewLabel: "View",
            userListsTitle: "Your Lists",
            viewAllLabel: "More",
            retryLabel: "Retry",
            userLists: userLists,
            progressTitle: "Progress",
            inProgressLabel: "In Progress",
            completedLabel: "Completed",
            progressEmptyLabel: "Nothing here yet",
            inProgress: .content(sampleShows),
            completed: .content(sampleShows),
            recentlyWatchedTitle: "Recently Watched",
            recentlyWatched: .content(sampleRecentShows),
            favoritesTitle: "Favorites",
            favorites: .content(sampleShows),
            unauthenticatedTitle: "Track your shows",
            footerDescription: "Sign in to sync your data.",
            isAuthenticated: true,
            featureItems: sampleFeatureItems
        )
    }

    func test_ProfileScreen_Authenticated() {
        ProfileScreen(
            state: authenticatedState(userLists: .content(sampleLists)),
            onSettingsClicked: {},
            onProviderSelected: { _ in }
        )
        .appPreview()
        .assertSnapshot(layout: .defaultDevice, testName: "ProfileScreen_Authenticated")
    }

    func test_ProfileScreen_UserListsWithMore() {
        ProfileScreen(
            state: authenticatedState(userLists: .content(manyLists)),
            onSettingsClicked: {},
            onProviderSelected: { _ in }
        )
        .appPreview()
        .assertSnapshot(layout: .defaultDevice, testName: "ProfileScreen_UserListsWithMore")
    }

    func test_ProfileScreen_UserListsEmpty() {
        ProfileScreen(
            state: authenticatedState(userLists: .empty),
            onSettingsClicked: {},
            onProviderSelected: { _ in }
        )
        .appPreview()
        .assertSnapshot(layout: .defaultDevice, testName: "ProfileScreen_UserListsEmpty")
    }

    func test_ListCollageCard() {
        HStack(spacing: 12) {
            ListCollageCard(
                list: SwiftProfileList(
                    id: 1,
                    name: "Watchlist",
                    itemCountLabel: "24 shows",
                    posterUrls: ["a", "b", "c", "d"]
                ),
                onClick: {}
            )
            ListCollageCard(
                list: SwiftProfileList(id: 2, name: "Favorites", itemCountLabel: "12 shows", posterUrls: ["e", "f"]),
                onClick: {}
            )
        }
        .padding()
        .appPreview()
        .assertSnapshot(layout: .sizeThatFits, testName: "ListCollageCard")
    }

    func test_ProfileScreen_UserListsError() {
        ProfileScreen(
            state: authenticatedState(userLists: .error("Failed to load lists")),
            onSettingsClicked: {},
            onProviderSelected: { _ in }
        )
        .appPreview()
        .assertSnapshot(layout: .defaultDevice, testName: "ProfileScreen_UserListsError")
    }

    // MARK: - Progress Section

    private func progressSection(
        inProgress: SwiftSectionState<SwiftProfileShow>,
        completed: SwiftSectionState<SwiftProfileShow>
    ) -> some View {
        ProgressSectionView(
            inProgress: inProgress,
            completed: completed,
            title: "Progress",
            inProgressLabel: "In Progress",
            completedLabel: "Completed",
            emptyLabel: "Nothing here yet",
            retryLabel: "Retry",
            onShowClick: { _ in },
            onRetry: {}
        )
        .padding(.vertical)
    }

    func test_ProgressSection_Content() {
        progressSection(inProgress: .content(sampleShows), completed: .content(sampleShows))
            .appPreview()
            .assertSnapshot(layout: .defaultDevice, testName: "ProgressSection_Content")
    }

    func test_ProgressSection_Loading() {
        progressSection(inProgress: .loading, completed: .loading)
            .appPreview()
            .assertSnapshot(layout: .defaultDevice, testName: "ProgressSection_Loading")
    }

    func test_ProgressSection_Error() {
        progressSection(inProgress: .content(sampleShows), completed: .error("Failed to load shows"))
            .appPreview()
            .assertSnapshot(layout: .defaultDevice, testName: "ProgressSection_Error")
    }

    func test_ProgressSection_EmptyFilter() {
        progressSection(inProgress: .content(sampleShows), completed: .empty)
            .appPreview()
            .assertSnapshot(layout: .defaultDevice, testName: "ProgressSection_EmptyFilter")
    }

    // MARK: - Recently Watched Section

    private func recentlyWatchedSection(
        recentlyWatched: SwiftSectionState<SwiftProfileRecentShow>
    ) -> some View {
        RecentlyWatchedSectionView(
            recentlyWatched: recentlyWatched,
            title: "Recently Watched",
            retryLabel: "Retry",
            onShowClick: { _ in },
            onRetry: {}
        )
        .padding(.vertical)
    }

    func test_RecentlyWatchedSection_Content() {
        recentlyWatchedSection(recentlyWatched: .content(sampleRecentShows))
            .appPreview()
            .assertSnapshot(layout: .defaultDevice, testName: "RecentlyWatchedSection_Content")
    }

    func test_RecentlyWatchedSection_Loading() {
        recentlyWatchedSection(recentlyWatched: .loading)
            .appPreview()
            .assertSnapshot(layout: .defaultDevice, testName: "RecentlyWatchedSection_Loading")
    }

    func test_RecentlyWatchedSection_Error() {
        recentlyWatchedSection(recentlyWatched: .error("Failed to load history"))
            .appPreview()
            .assertSnapshot(layout: .defaultDevice, testName: "RecentlyWatchedSection_Error")
    }

    // MARK: - Favorites Section

    private func favoritesSection(
        favorites: SwiftSectionState<SwiftProfileShow>
    ) -> some View {
        FavoritesSectionView(
            favorites: favorites,
            title: "Favorites",
            retryLabel: "Retry",
            onShowClick: { _ in },
            onRetry: {}
        )
        .padding(.vertical)
    }

    func test_FavoritesSection_Content() {
        favoritesSection(favorites: .content(sampleShows))
            .appPreview()
            .assertSnapshot(layout: .sizeThatFits, testName: "FavoritesSection_Content")
    }

    func test_FavoritesSection_Loading() {
        favoritesSection(favorites: .loading)
            .appPreview()
            .assertSnapshot(layout: .sizeThatFits, testName: "FavoritesSection_Loading")
    }

    func test_FavoritesSection_Error() {
        favoritesSection(favorites: .error("Failed to load favorites"))
            .appPreview()
            .assertSnapshot(layout: .sizeThatFits, testName: "FavoritesSection_Error")
    }
}
