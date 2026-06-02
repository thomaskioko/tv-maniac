import Components
import DesignSystem
import Models
import ShowDetails
import SnapshotTestingLib
import SwiftUI
import XCTest

class CastListViewTest: SnapshotTestCase {
    func test_CastListView() {
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
        .appPreview()
        .assertSnapshot(testName: "CastListView")
    }
}
