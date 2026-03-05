import SnapshotTestingLib
import SwiftUI
import SwiftUIComponents
import XCTest

class SeasonDetailsScreenTest: SnapshotTestCase {
    private let sampleEpisodes: [SwiftEpisode] = [
        SwiftEpisode(
            episodeId: 1,
            title: "Winter Is Coming",
            overview: "Ned Stark is asked to become the Hand of the King.",
            imageUrl: nil,
            seasonNumber: 1,
            episodeNumber: 1,
            isWatched: true,
            isEpisodeUpdating: false,
            daysUntilAir: nil,
            hasPreviousUnwatched: false,
            hasAired: true
        ),
        SwiftEpisode(
            episodeId: 2,
            title: "The Kingsroad",
            overview: "The Lannisters plot to ensure Bran's silence.",
            imageUrl: nil,
            seasonNumber: 1,
            episodeNumber: 2,
            isWatched: false,
            isEpisodeUpdating: false,
            daysUntilAir: nil,
            hasPreviousUnwatched: false,
            hasAired: true
        ),
        SwiftEpisode(
            episodeId: 3,
            title: "Lord Snow",
            overview: "Jon Snow attempts to find his place at the Wall.",
            imageUrl: nil,
            seasonNumber: 1,
            episodeNumber: 3,
            isWatched: false,
            isEpisodeUpdating: false,
            daysUntilAir: nil,
            hasPreviousUnwatched: true,
            hasAired: true
        ),
    ]

    private let sampleCasts: [SwiftCast] = [
        SwiftCast(castId: 1, name: "Sean Bean", characterName: "Ned Stark", profileUrl: nil),
        SwiftCast(castId: 2, name: "Emilia Clarke", characterName: "Daenerys Targaryen", profileUrl: nil),
    ]

    func test_SeasonDetailsScreen_Default() {
        SeasonDetailsScreen(
            seasonName: "Season 1",
            imageUrl: nil,
            seasonOverview: "Ned Stark, Lord of Winterfell, is asked by his old friend King Robert to serve as Hand of the King.",
            episodeCount: 10,
            watchProgress: 0.3,
            expandEpisodeItems: true,
            isSeasonWatched: false,
            isRefreshing: false,
            showError: false,
            seasonImages: [],
            episodes: sampleEpisodes,
            casts: sampleCasts,
            errorTitle: "Something went wrong",
            errorRetryText: "Retry",
            overviewTitle: "Season Overview",
            episodesTitle: "Episodes",
            seasonImagesCountFormat: { "\($0) Images" },
            dayLabelFormat: { "\($0) days" },
            toast: .constant(nil),
            showGallery: .constant(false),
            onBack: {},
            onRetry: {},
            onGalleryTap: {},
            onEpisodeHeaderClicked: {},
            onWatchedStateClicked: {},
            onEpisodeWatchToggle: { _ in }
        )
        .themedPreview()
        .assertSnapshot(layout: .defaultDevice, testName: "SeasonDetailsScreen_Default")
    }

    func test_SeasonDetailsScreen_Error() {
        SeasonDetailsScreen(
            seasonName: "Season 1",
            imageUrl: nil,
            seasonOverview: "",
            episodeCount: 0,
            watchProgress: 0,
            expandEpisodeItems: false,
            isSeasonWatched: false,
            isRefreshing: false,
            showError: true,
            seasonImages: [],
            episodes: [],
            casts: [],
            errorTitle: "Something went wrong",
            errorRetryText: "Retry",
            overviewTitle: "Season Overview",
            episodesTitle: "Episodes",
            seasonImagesCountFormat: { "\($0) Images" },
            dayLabelFormat: { "\($0) days" },
            toast: .constant(nil),
            showGallery: .constant(false),
            onBack: {},
            onRetry: {},
            onGalleryTap: {},
            onEpisodeHeaderClicked: {},
            onWatchedStateClicked: {},
            onEpisodeWatchToggle: { _ in }
        )
        .themedPreview()
        .assertSnapshot(layout: .defaultDevice, testName: "SeasonDetailsScreen_Error")
    }

    func test_SeasonDetailsScreen_FullyWatched() {
        let watchedEpisodes = sampleEpisodes.map { ep in
            SwiftEpisode(
                episodeId: ep.episodeId,
                title: ep.title,
                overview: ep.overview,
                imageUrl: ep.imageUrl,
                seasonNumber: ep.seasonNumber,
                episodeNumber: ep.episodeNumber,
                isWatched: true,
                isEpisodeUpdating: false,
                daysUntilAir: nil,
                hasPreviousUnwatched: false,
                hasAired: true
            )
        }

        SeasonDetailsScreen(
            seasonName: "Season 1",
            imageUrl: nil,
            seasonOverview: "All episodes watched.",
            episodeCount: 3,
            watchProgress: 1.0,
            expandEpisodeItems: true,
            isSeasonWatched: true,
            isRefreshing: false,
            showError: false,
            seasonImages: [],
            episodes: watchedEpisodes,
            casts: sampleCasts,
            errorTitle: "Something went wrong",
            errorRetryText: "Retry",
            overviewTitle: "Season Overview",
            episodesTitle: "Episodes",
            seasonImagesCountFormat: { "\($0) Images" },
            dayLabelFormat: { "\($0) days" },
            toast: .constant(nil),
            showGallery: .constant(false),
            onBack: {},
            onRetry: {},
            onGalleryTap: {},
            onEpisodeHeaderClicked: {},
            onWatchedStateClicked: {},
            onEpisodeWatchToggle: { _ in }
        )
        .themedPreview()
        .assertSnapshot(layout: .defaultDevice, testName: "SeasonDetailsScreen_FullyWatched")
    }
}
