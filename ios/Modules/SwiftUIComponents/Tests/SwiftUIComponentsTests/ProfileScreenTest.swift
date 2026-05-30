import DesignSystem
import SnapshotTestingLib
import SwiftUI
import SwiftUIComponents
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
                signInLabel: "Sign In with Trakt",
                featureItems: sampleFeatureItems
            ),
            onSettingsClicked: {},
            onLoginClicked: {}
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
                signInLabel: "Sign In with Trakt",
                featureItems: sampleFeatureItems
            ),
            onSettingsClicked: {},
            onLoginClicked: {}
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
            unauthenticatedTitle: "Track your shows",
            footerDescription: "Sign in to sync your data.",
            signInLabel: "Sign In with Trakt",
            featureItems: sampleFeatureItems
        )
    }

    func test_ProfileScreen_Authenticated() {
        ProfileScreen(
            state: authenticatedState(userLists: .content(sampleLists)),
            onSettingsClicked: {},
            onLoginClicked: {}
        )
        .appPreview()
        .assertSnapshot(layout: .defaultDevice, testName: "ProfileScreen_Authenticated")
    }

    func test_ProfileScreen_UserListsWithMore() {
        ProfileScreen(
            state: authenticatedState(userLists: .content(manyLists)),
            onSettingsClicked: {},
            onLoginClicked: {}
        )
        .appPreview()
        .assertSnapshot(layout: .defaultDevice, testName: "ProfileScreen_UserListsWithMore")
    }

    func test_ProfileScreen_UserListsEmpty() {
        ProfileScreen(
            state: authenticatedState(userLists: .empty),
            onSettingsClicked: {},
            onLoginClicked: {}
        )
        .appPreview()
        .assertSnapshot(layout: .defaultDevice, testName: "ProfileScreen_UserListsEmpty")
    }

    func test_ProfileScreen_UserListsError() {
        ProfileScreen(
            state: authenticatedState(userLists: .error("Failed to load lists")),
            onSettingsClicked: {},
            onLoginClicked: {}
        )
        .appPreview()
        .assertSnapshot(layout: .defaultDevice, testName: "ProfileScreen_UserListsError")
    }
}
