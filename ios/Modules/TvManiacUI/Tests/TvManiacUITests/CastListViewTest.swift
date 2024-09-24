import SnapshotTestingLib
import SwiftUI
import TvManiacUI
import XCTest

class CastListViewTest: XCTestCase {
    func test_HeaderView() {
        CastListView(
            casts: [
                .init(
                    castId: 123,
                    name: "Rosario Dawson",
                    characterName: "Claire Temple",
                    profileUrl: ""
                ),
                .init(
                    castId: 1234,
                    name: "Hailee Steinfeld",
                    characterName: "Hailee Steinfeld",
                    profileUrl: ""
                ),
                .init(
                    castId: 1235,
                    name: "内田夕夜",
                    characterName: "Yuuya Uchida",
                    profileUrl: ""
                ),
            ]
        )
        .background(Color.background)
        .assertSnapshot(testName: "CastListView")
    }
}
