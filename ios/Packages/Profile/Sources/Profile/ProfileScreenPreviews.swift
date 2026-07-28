import Components
import DesignSystem
import Models
import SwiftUI
import TvManiac

struct StatValueText: View {
    @Environment(\.appTheme) private var appTheme

    let count: Int

    var body: some View {
        AnimatedCountText(count: count)
            .textStyle(appTheme.typography.headlineLarge)
            .foregroundStyle(.appOnSurface)
            .shadow(color: .black.opacity(0.3), radius: 4, x: 0, y: 2)
    }
}

enum ProfileDimensions {
    static let imageHeight: CGFloat = 315
    static let maxInlineLists: Int = 4
}

struct ProfileScrollOffsetKey: PreferenceKey {
    static var defaultValue: CGFloat = 0

    static func reduce(value: inout CGFloat, nextValue: () -> CGFloat) {
        value = nextValue()
    }
}

private let previewFeatureItems: [SwiftFeatureItem] = [
    SwiftFeatureItem(
        id: "discover",
        iconName: "magnifyingglass",
        title: "Discover",
        description: "Browse over one million movies and TV shows."
    ),
    SwiftFeatureItem(
        id: "track",
        iconName: "tv",
        title: "Track",
        description: "Check-in, mark watched and manage your watch history."
    ),
    SwiftFeatureItem(
        id: "manage",
        iconName: "rectangle.stack",
        title: "Watchlist",
        description: "Create custom lists, personalize just the way you like."
    ),
    SwiftFeatureItem(
        id: "more",
        iconName: "sparkles",
        title: "More",
        description: "More features coming soon."
    ),
]

private let previewShows: [SwiftProfileShow] = [
    SwiftProfileShow(id: 1, title: "Breaking Bad", posterUrl: nil),
    SwiftProfileShow(id: 2, title: "Game of Thrones", posterUrl: nil),
    SwiftProfileShow(id: 3, title: "Stranger Things", posterUrl: nil),
]

private let previewRecentShows: [SwiftProfileRecentShow] = [
    SwiftProfileRecentShow(showId: 1, title: "Breaking Bad", posterUrl: nil, episodeLabel: "S5E14"),
    SwiftProfileRecentShow(showId: 2, title: "Game of Thrones", posterUrl: nil, episodeLabel: "S8E3"),
]

#Preview("Authenticated – Stats Present") {
    ProfileScreen(
        state: ProfileScreen.State(
            title: "Profile",
            isLoading: false,
            userProfile: SwiftProfileInfo(
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
            ),
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
            inProgress: .content(previewShows),
            completed: .content(previewShows),
            recentlyWatchedTitle: "Recently Watched",
            recentlyWatched: .content(previewRecentShows),
            favoritesTitle: "Favorites",
            favorites: .content(previewShows),
            unauthenticatedTitle: "Discover.\nTrack.\nWatchlist.\n& More ...",
            authTitle: "Connect & Sync Your Content",
            authDescription: "Save your progress, discover new titles, and sync across all devices.",
            isAuthenticated: true,
            featureItems: previewFeatureItems
        ),
        onSettingsClicked: {},
        onProviderSelected: { _ in }
    )
}

#Preview("Authenticated – Stats Hidden (Simkl)") {
    ProfileScreen(
        state: ProfileScreen.State(
            title: "Profile",
            isLoading: false,
            userProfile: SwiftProfileInfo(
                username: "simkl_user",
                fullName: "Jane Doe",
                avatarUrl: nil,
                backgroundUrl: nil,
                stats: nil
            ),
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
            inProgress: .content(previewShows),
            completed: .content(previewShows),
            recentlyWatchedTitle: "Recently Watched",
            recentlyWatched: .content(previewRecentShows),
            favoritesTitle: "Favorites",
            favorites: .content(previewShows),
            unauthenticatedTitle: "Discover.\nTrack.\nWatchlist.\n& More ...",
            authTitle: "Connect & Sync Your Content",
            authDescription: "Save your progress, discover new titles, and sync across all devices.",
            isAuthenticated: true,
            featureItems: previewFeatureItems
        ),
        onSettingsClicked: {},
        onProviderSelected: { _ in }
    )
}

#Preview("Loading") {
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
            unauthenticatedTitle: "Discover.\nTrack.\nWatchlist.\n& More ...",
            authTitle: "Connect & Sync Your Content",
            authDescription: "Save your progress, discover new titles, and sync across all devices.",
            isAuthenticated: false,
            featureItems: previewFeatureItems
        ),
        onSettingsClicked: {},
        onProviderSelected: { _ in }
    )
}

#Preview("Unauthenticated") {
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
            unauthenticatedTitle: "Discover.\nTrack.\nWatchlist.\n& More ...",
            authTitle: "Connect & Sync Your Content",
            authDescription: "Save your progress, discover new titles, and sync across all devices.",
            isAuthenticated: false,
            featureItems: previewFeatureItems,
            authProviders: [
                SwiftAuthProvider(id: "TRAKT", label: "Continue with Trakt", logoName: "TraktMono"),
                SwiftAuthProvider(id: "SIMKL", label: "Continue with Simkl", logoName: "SimklMono"),
            ]
        ),
        onSettingsClicked: {},
        onProviderSelected: { _ in }
    )
}
