import SnapshotTestingLib
import SwiftUI
import SwiftUIComponents
import XCTest

class ShowDetailsScreenTest: SnapshotTestCase {
    private let sampleGenres: [SwiftGenres] = [
        SwiftGenres(name: "Drama"),
        SwiftGenres(name: "Fantasy"),
        SwiftGenres(name: "Adventure"),
    ]

    private let sampleSeasons: [SwiftSeason] = [
        SwiftSeason(
            tvShowId: 100,
            seasonId: 1,
            seasonNumber: 1,
            name: "Season 1",
            watchedCount: 5,
            totalCount: 10,
            progressPercentage: 0.5
        ),
        SwiftSeason(
            tvShowId: 100,
            seasonId: 2,
            seasonNumber: 2,
            name: "Season 2",
            watchedCount: 0,
            totalCount: 10,
            progressPercentage: 0
        ),
    ]

    private let sampleCast: [SwiftCast] = [
        SwiftCast(castId: 1, name: "Pedro Pascal", characterName: "Joel Miller", profileUrl: nil),
        SwiftCast(castId: 2, name: "Bella Ramsey", characterName: "Ellie Williams", profileUrl: nil),
    ]

    func test_ShowDetailsScreen_Default() {
        ShowDetailsScreen(
            title: "The Last of Us",
            overview: "Twenty years after modern civilization has been destroyed, Joel is hired to smuggle Ellie out of an oppressive quarantine zone.",
            backdropImageUrl: nil,
            posterImageUrl: nil,
            status: "Returning Series",
            year: "2023",
            language: "en",
            rating: 8.8,
            isInLibrary: false,
            isRefreshing: false,
            openTrailersInYoutube: false,
            selectedSeasonIndex: 0,
            watchedEpisodesCount: 0,
            totalEpisodesCount: 20,
            genreList: sampleGenres,
            seasonList: sampleSeasons,
            providerList: [],
            trailerList: [],
            castsList: sampleCast,
            similarShows: [],
            continueTrackingEpisodes: [],
            continueTrackingScrollIndex: 0,
            continueTrackingTitle: "Continue Tracking",
            dayLabelFormat: { "\($0) days" },
            tbdLabel: "TBD",
            trackLabel: "Following",
            stopTrackingLabel: "Unfollow",
            addToListLabel: "Add to List",
            similarShowsTitle: "Similar Shows",
            seasonDetailsTitle: "Season Details",
            showSeasonDetailsHeader: true,
            seasonCountFormat: { "\($0) Seasons" },
            episodesWatchedFormat: { "\($0)/\($1) Watched" },
            episodesLeftFormat: { "\($0) Left" },
            upToDateLabel: "Up to date",
            toast: .constant(nil),
            onBack: {},
            onRefresh: {},
            onAddToCustomList: {},
            onAddToLibrary: {},
            onSeasonClicked: { _, _ in },
            onShowClicked: { _ in },
            onMarkEpisodeWatched: { _ in }
        )
        .themedPreview()
        .assertSnapshot(layout: .defaultDevice, testName: "ShowDetailsScreen_Default")
    }

    func test_ShowDetailsScreen_InLibrary() {
        ShowDetailsScreen(
            title: "The Last of Us",
            overview: "Twenty years after modern civilization has been destroyed.",
            backdropImageUrl: nil,
            posterImageUrl: nil,
            status: "Returning Series",
            year: "2023",
            language: "en",
            rating: 8.8,
            isInLibrary: true,
            isRefreshing: false,
            openTrailersInYoutube: false,
            selectedSeasonIndex: 0,
            watchedEpisodesCount: 5,
            totalEpisodesCount: 20,
            genreList: sampleGenres,
            seasonList: sampleSeasons,
            providerList: [],
            trailerList: [],
            castsList: sampleCast,
            similarShows: [],
            continueTrackingEpisodes: [],
            continueTrackingScrollIndex: 0,
            continueTrackingTitle: "Continue Tracking",
            dayLabelFormat: { "\($0) days" },
            tbdLabel: "TBD",
            trackLabel: "Following",
            stopTrackingLabel: "Unfollow",
            addToListLabel: "Add to List",
            similarShowsTitle: "Similar Shows",
            seasonDetailsTitle: "Season Details",
            showSeasonDetailsHeader: true,
            seasonCountFormat: { "\($0) Seasons" },
            episodesWatchedFormat: { "\($0)/\($1) Watched" },
            episodesLeftFormat: { "\($0) Left" },
            upToDateLabel: "Up to date",
            toast: .constant(nil),
            onBack: {},
            onRefresh: {},
            onAddToCustomList: {},
            onAddToLibrary: {},
            onSeasonClicked: { _, _ in },
            onShowClicked: { _ in },
            onMarkEpisodeWatched: { _ in }
        )
        .themedPreview()
        .assertSnapshot(layout: .defaultDevice, testName: "ShowDetailsScreen_InLibrary")
    }
}
