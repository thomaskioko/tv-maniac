import SnapshotTestingLib
import SwiftUI
import SwiftUIComponents
import XCTest

class ProfileScreenTest: SnapshotTestCase {
    private let sampleFeatureItems: [SwiftFeatureItem] = [
        SwiftFeatureItem(id: "discover", iconName: "magnifyingglass", title: "Discover Shows", description: "Find new shows to watch"),
        SwiftFeatureItem(id: "track", iconName: "tv", title: "Track Progress", description: "Keep track of what you've watched"),
        SwiftFeatureItem(id: "manage", iconName: "rectangle.stack", title: "Manage Library", description: "Organize your shows"),
        SwiftFeatureItem(id: "more", iconName: "sparkles", title: "And More", description: "Get personalized recommendations"),
    ]

    func test_ProfileScreen_Loading() {
        ProfileScreen(
            title: "Profile",
            isLoading: true,
            userProfile: nil,
            editButtonLabel: "Edit Profile",
            statsTitle: "Statistics",
            watchTimeLabel: "Watch Time",
            monthsLabel: "Months",
            daysLabel: "Days",
            hoursLabel: "Hours",
            episodesWatchedLabel: "Episodes Watched",
            unauthenticatedTitle: "Track your shows",
            footerDescription: "Sign in to sync your data.",
            signInLabel: "Sign In with Trakt",
            featureItems: sampleFeatureItems,
            onSettingsClicked: {},
            onLoginClicked: {}
        )
        .themedPreview()
        .assertSnapshot(layout: .defaultDevice, testName: "ProfileScreen_Loading")
    }

    func test_ProfileScreen_Unauthenticated() {
        ProfileScreen(
            title: "Profile",
            isLoading: false,
            userProfile: nil,
            editButtonLabel: "Edit Profile",
            statsTitle: "Statistics",
            watchTimeLabel: "Watch Time",
            monthsLabel: "Months",
            daysLabel: "Days",
            hoursLabel: "Hours",
            episodesWatchedLabel: "Episodes Watched",
            unauthenticatedTitle: "Track your shows",
            footerDescription: "Sign in to sync your data across devices.",
            signInLabel: "Sign In with Trakt",
            featureItems: sampleFeatureItems,
            onSettingsClicked: {},
            onLoginClicked: {}
        )
        .themedPreview()
        .assertSnapshot(layout: .defaultDevice, testName: "ProfileScreen_Unauthenticated")
    }

    func test_ProfileScreen_Authenticated() {
        let profile = SwiftProfileInfo(
            username: "tvmaniac_user",
            fullName: "John Doe",
            avatarUrl: nil,
            backgroundUrl: nil,
            stats: SwiftProfileStats(
                months: 2,
                days: 15,
                hours: 8,
                episodesWatched: 1250
            )
        )

        ProfileScreen(
            title: "Profile",
            isLoading: false,
            userProfile: profile,
            editButtonLabel: "Edit Profile",
            statsTitle: "Statistics",
            watchTimeLabel: "Watch Time",
            monthsLabel: "Months",
            daysLabel: "Days",
            hoursLabel: "Hours",
            episodesWatchedLabel: "Episodes Watched",
            unauthenticatedTitle: "Track your shows",
            footerDescription: "Sign in to sync your data.",
            signInLabel: "Sign In with Trakt",
            featureItems: sampleFeatureItems,
            onSettingsClicked: {},
            onLoginClicked: {}
        )
        .themedPreview()
        .assertSnapshot(layout: .defaultDevice, testName: "ProfileScreen_Authenticated")
    }
}
