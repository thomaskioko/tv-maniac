import SnapshotTestingLib
import SwiftUI
import TvManiacUI
import XCTest

class EpisodeListViewTest: XCTestCase {
    func test_EpisodeListView() {
        EpisodeListView(
            showingAlert: false,
            episodeCount: 3,
            watchProgress: 0.4,
            expandEpisodeItems: false,
            showSeasonWatchStateDialog: false,
            isSeasonWatched: false,
            items: [
                .init(
                    episodeId: 123,
                    title: "E1 Model 101",
                    overview: "In 1997, a haunted scientist brushes his family aside for an all-consuming project. In 2022, a renegade fighter battles a powerful robot for vital data.",
                    imageUrl: "https://image.tmdb.org/t/p/w780/https://image.tmdb.org/t/p/w780/8rjILRAlcvI9y7vJuH9yNjKYhta.jpg"
                ),
                .init(
                    episodeId: 1234,
                    title: "E2 Model 102",
                    overview: "Eiko and the Terminator arrive in 1997 with identical missions: find Dr. Malcolm Lee. Meanwhile, Lee's three children sneak out of their apartment.",
                    imageUrl: "https://image.tmdb.org/t/p/w780/https://image.tmdb.org/t/p/w780/xfy7Z5IL5QMJo9XCx69s3HlP8Sl.jpg"
                ),
                .init(
                    episodeId: 1233,
                    title: "E3 Model 103",
                    overview: "Malcolm confides in Kokoro about his recurring nightmare. The three children continue their underground trek, unaware of looming danger.",
                    imageUrl: "https://image.tmdb.org/t/p/w780/https://image.tmdb.org/t/p/w780/uNXoR4PR4Uh2ymXz12Z1mhwZoJS.jpg"
                )
            ],
            onEpisodeHeaderClicked: {},
            onWatchedStateClicked: {}
        )
        .background(Color.background)
        .assertSnapshot(layout: .defaultDevice, testName: "EpisodeListView")
    }
}
