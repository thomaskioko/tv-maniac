import SnapshotTestingLib
import SwiftUI
import SwiftUIComponents
import XCTest

class ShowInfoViewTest: SnapshotTestCase {
    func test_ShowInfoView() {
        ShowInfoView(
            isFollowed: true,
            openTrailersInYoutube: false,
            status: "Ended",
            watchedEpisodesCount: 7,
            totalEpisodesCount: 12,
            genreList: genreList,
            seasonList: seasonList,
            providerList: providerList,
            trailerList: trailerList,
            castsList: castsList,
            similarShows: showList,
            continueTrackingTitle: "Continue tracking",
            dayLabelFormat: { count in count == 1 ? "day" : "days" },
            trackLabel: "Track",
            stopTrackingLabel: "Stop Tracking",
            addToListLabel: "Add To List",
            similarShowsTitle: "Similar Shows",
            seasonDetailsTitle: "Season Details",
            seasonCountFormat: { count in count == 1 ? "\(count) Season" : "\(count) Seasons" },
            episodesWatchedFormat: { watched, total in "\(watched) of \(total) episodes watched" },
            episodesLeftFormat: { count in count == 1 ? "\(count) episode left to watch" : "\(count) episodes left to watch" },
            upToDateLabel: "You're up-to-date",
            onAddToCustomList: {},
            onAddToLibrary: {},
            onSeasonClicked: { _, _ in },
            onShowClicked: { _ in }
        )
        .themedPreview()
        .assertSnapshot(testName: "ShowInfoView")
    }

    private var genreList: [SwiftGenres] {
        [.init(name: "Sci-Fi"), .init(name: "Horror"), .init(name: "Action")]
    }

    private var seasonList: [SwiftSeason] {
        [
            .init(tvShowId: 23, seasonId: 23, seasonNumber: 1, name: "Season 1", watchedCount: 6, totalCount: 6, progressPercentage: 1.0),
            .init(tvShowId: 123, seasonId: 123, seasonNumber: 2, name: "Season 2", watchedCount: 1, totalCount: 6, progressPercentage: 0.17),
        ]
    }

    private var providerList: [SwiftProviders] {
        [.init(providerId: 123, logoUrl: ""), .init(providerId: 1233, logoUrl: ""), .init(providerId: 23, logoUrl: "")]
    }

    private var trailerList: [SwiftTrailer] {
        [
            .init(showTmdbId: 123, key: "XZ8daibM3AE", name: "Series Trailer", youtubeThumbnailUrl: ""),
            .init(showTmdbId: 1234, key: "XZ8daibM3AE", name: "Series Trailer", youtubeThumbnailUrl: ""),
        ]
    }

    private var castsList: [SwiftCast] {
        [
            .init(castId: 123, name: "Rosario Dawson", characterName: "Claire Temple", profileUrl: ""),
            .init(castId: 1234, name: "Hailee Steinfeld", characterName: "Hailee Steinfeld", profileUrl: ""),
            .init(castId: 1235, name: "内田夕夜", characterName: "Yuuya Uchida", profileUrl: ""),
        ]
    }

    private var showList: [SwiftShow] {
        [
            .init(traktId: 1234, title: "Arcane", posterUrl: "", backdropUrl: nil, inLibrary: false),
            .init(traktId: 123, title: "The Lord of the Rings: The Rings of Power", posterUrl: "", backdropUrl: nil, inLibrary: false),
            .init(traktId: 12346, title: "Kaos", posterUrl: "", backdropUrl: nil, inLibrary: false),
        ]
    }
}
