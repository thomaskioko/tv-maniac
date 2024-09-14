import SnapshotTestingLib
import SwiftUI
import TvManiacUI
import XCTest


class ProviderListViewTest: XCTestCase {
    func test_ProviderListView(){
        ProviderListView(
            items: [
                .init(
                    providerId: 123,
                    logoUrl: "https://image.tmdb.org/t/p/w780/https://image.tmdb.org/t/p/w780/4KAy34EHvRM25Ih8wb82AuGU7zJ.png"
                ),
                .init(
                    providerId: 1233,
                    logoUrl: "https://image.tmdb.org/t/p/w780/https://image.tmdb.org/t/p/w780/alqLicR1ZMHMaZGP3xRQxn9sq7p.png"
                ),
                .init(
                    providerId: 23,
                    logoUrl: "https://image.tmdb.org/t/p/w780/https://image.tmdb.org/t/p/w780/wwemzKWzjKYJFfCeiB57q3r4Bcm.png"
                ),
            ]
        )
        .background(Color.background)
        .assertSnapshot(testName: "ProviderList")
    }
}
