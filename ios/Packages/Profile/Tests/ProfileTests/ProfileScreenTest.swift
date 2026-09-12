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
            title: "Discover",
            description: "Browse over one million movies and TV shows and see \"Where to Watch\" them."
        ),
        SwiftFeatureItem(
            id: "track",
            iconName: "tv",
            title: "Track",
            description: "Check-in, mark watch and manage your all-time watch history."
        ),
        SwiftFeatureItem(
            id: "manage",
            iconName: "rectangle.stack",
            title: "Watchlist",
            description: "Create custom list, personalize it just the way you like it."
        ),
        SwiftFeatureItem(
            id: "more",
            iconName: "sparkles",
            title: "More",
            description: "More features coming soon."
        ),
    ]

    func test_ProfileScreen_Loading() {
        let view = ProfileScreen(
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
                unauthenticatedTitle: "Discover.\nTrack.\nWatchlist.\n& More ...",
                authTitle: "Connect & Sync Your Content",
                authDescription: "Save your progress, discover new titles, and sync your content across all devices.",
                isAuthenticated: false,
                featureItems: sampleFeatureItems
            ),
            onSettingsClicked: {},
            onProviderSelected: { _ in }
        )
        .appPreview()
        view.assertSnapshot(layout: .defaultDevice, testName: "ProfileScreen_Loading")
        view.assertSnapshot(layout: .defaultDevice, liquidGlass: true, testName: "ProfileScreen_Loading")
    }

    func test_ProfileScreen_Unauthenticated() {
        let view = ProfileScreen(
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
                unauthenticatedTitle: "Discover.\nTrack.\nWatchlist.\n& More ...",
                authTitle: "Connect & Sync Your Content",
                authDescription: "Save your progress, discover new titles, and sync your content across all devices.",
                isAuthenticated: false,
                featureItems: sampleFeatureItems,
                authProviders: [
                    SwiftAuthProvider(id: "TRAKT", label: "Continue with Trakt", logoName: "TraktMono"),
                    SwiftAuthProvider(id: "SIMKL", label: "Continue with Simkl", logoName: "SimklMono"),
                ]
            ),
            onSettingsClicked: {},
            onProviderSelected: { _ in }
        )
        .appPreview()
        view.assertSnapshot(layout: .defaultDevice, testName: "ProfileScreen_Unauthenticated")
        view.assertSnapshot(layout: .defaultDevice, liquidGlass: true, testName: "ProfileScreen_Unauthenticated")
    }

    private let sampleLists: [ListCollageItem] = [
        ListCollageItem(id: 1, name: "Watchlist", itemCountLabel: "24 shows", posterUrls: ["a", "b", "c", "d"]),
        ListCollageItem(id: 2, name: "Favorites", itemCountLabel: "2 shows", posterUrls: ["e", "f"]),
        ListCollageItem(id: 3, name: "New List", itemCountLabel: "3 shows", posterUrls: []),
    ]

    private let manyLists: [ListCollageItem] = (1 ... 5).map { index in
        ListCollageItem(
            id: Int64(index),
            name: "List \(index)",
            itemCountLabel: "\(index) shows",
            posterUrls: ["a", "b", "c", "d"]
        )
    }

    private func authenticatedProfile(stats: SwiftProfileStats? = SwiftProfileStats(
        showsWatched: "87",
        episodesWatched: "1,250",
        months: 2,
        days: 15,
        hours: 8,
        listCount: 12
    )) -> SwiftProfileInfo {
        SwiftProfileInfo(
            username: "tvmaniac_user",
            fullName: "John Doe",
            avatarUrl: nil,
            backgroundUrl: nil,
            stats: stats
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

    private func authenticatedState(
        userLists: SwiftSectionState<ListCollageItem>,
        profile: SwiftProfileInfo? = nil
    ) -> ProfileScreen.State {
        ProfileScreen.State(
            title: "Profile",
            isLoading: false,
            userProfile: profile ?? authenticatedProfile(),
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
            unauthenticatedTitle: "Discover.\nTrack.\nWatchlist.\n& More ...",
            authTitle: "Connect & Sync Your Content",
            authDescription: "Save your progress, discover new titles, and sync your content across all devices.",
            isAuthenticated: true,
            featureItems: sampleFeatureItems
        )
    }

    func test_ProfileScreen_Authenticated() {
        let view = ProfileScreen(
            state: authenticatedState(userLists: .content(sampleLists)),
            onSettingsClicked: {},
            onProviderSelected: { _ in }
        )
        .appPreview()
        view.assertSnapshot(layout: .defaultDevice, testName: "ProfileScreen_Authenticated")
        view.assertSnapshot(layout: .defaultDevice, liquidGlass: true, testName: "ProfileScreen_Authenticated")
    }

    func test_ProfileScreen_Authenticated_StatsHidden() {
        let view = ProfileScreen(
            state: authenticatedState(
                userLists: .content(sampleLists),
                profile: authenticatedProfile(stats: nil)
            ),
            onSettingsClicked: {},
            onProviderSelected: { _ in }
        )
        .appPreview()
        view.assertSnapshot(layout: .defaultDevice, testName: "ProfileScreen_Authenticated_StatsHidden")
        view.assertSnapshot(layout: .defaultDevice, liquidGlass: true, testName: "ProfileScreen_Authenticated_StatsHidden")
    }

    func test_ProfileScreen_UserListsWithMore() {
        let view = ProfileScreen(
            state: authenticatedState(userLists: .content(manyLists)),
            onSettingsClicked: {},
            onProviderSelected: { _ in }
        )
        .appPreview()
        view.assertSnapshot(layout: .defaultDevice, testName: "ProfileScreen_UserListsWithMore")
        view.assertSnapshot(layout: .defaultDevice, liquidGlass: true, testName: "ProfileScreen_UserListsWithMore")
    }

    func test_ProfileScreen_UserListsEmpty() {
        let view = ProfileScreen(
            state: authenticatedState(userLists: .empty),
            onSettingsClicked: {},
            onProviderSelected: { _ in }
        )
        .appPreview()
        view.assertSnapshot(layout: .defaultDevice, testName: "ProfileScreen_UserListsEmpty")
        view.assertSnapshot(layout: .defaultDevice, liquidGlass: true, testName: "ProfileScreen_UserListsEmpty")
    }

    func test_ListCollageCard() {
        HStack(spacing: 12) {
            ListCollageCard(
                list: ListCollageItem(
                    id: 1,
                    name: "Watchlist",
                    itemCountLabel: "24 shows",
                    posterUrls: ["a", "b", "c", "d"]
                ),
                onClick: {}
            )
            ListCollageCard(
                list: ListCollageItem(id: 2, name: "Favorites", itemCountLabel: "12 shows", posterUrls: ["e", "f"]),
                onClick: {}
            )
        }
        .padding()
        .appPreview()
        .assertSnapshot(layout: .sizeThatFits, testName: "ListCollageCard")
    }

    func test_ProfileScreen_UserListsError() {
        let view = ProfileScreen(
            state: authenticatedState(userLists: .error("Failed to load lists")),
            onSettingsClicked: {},
            onProviderSelected: { _ in }
        )
        .appPreview()
        view.assertSnapshot(layout: .defaultDevice, testName: "ProfileScreen_UserListsError")
        view.assertSnapshot(layout: .defaultDevice, liquidGlass: true, testName: "ProfileScreen_UserListsError")
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
