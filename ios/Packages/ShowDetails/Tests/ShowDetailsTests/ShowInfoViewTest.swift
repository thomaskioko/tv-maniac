import Components
import DesignSystem
import Models
import ShowDetails
import SnapshotTestingLib
import SwiftUI
import XCTest

class ShowInfoViewTest: SnapshotTestCase {
    func test_ShowInfoView_Followed() {
        ShowInfoView(
            isFollowed: true,
            canAddToList: true,
            genres: [.init(name: "Sci-Fi"), .init(name: "Horror"), .init(name: "Action")],
            trackLabel: "Track",
            stopTrackingLabel: "Stop Tracking",
            addToListLabel: "Add To List",
            rateLabel: "Rate",
            onAddToLibrary: {},
            onAddToCustomList: {},
            onRate: {}
        )
        .padding()
        .appPreview()
        .assertSnapshot(testName: "ShowInfoView_Followed")
    }

    func test_ShowInfoView_NotFollowed() {
        ShowInfoView(
            isFollowed: false,
            canAddToList: true,
            genres: [.init(name: "Drama"), .init(name: "Fantasy"), .init(name: "Adventure")],
            trackLabel: "Track",
            stopTrackingLabel: "Stop Tracking",
            addToListLabel: "Add To List",
            rateLabel: "Rate",
            onAddToLibrary: {},
            onAddToCustomList: {},
            onRate: {}
        )
        .padding()
        .appPreview()
        .assertSnapshot(testName: "ShowInfoView_NotFollowed")
    }

    func test_ShowInfoView_SimklNoList() {
        ShowInfoView(
            isFollowed: true,
            canAddToList: false,
            genres: [.init(name: "Sci-Fi"), .init(name: "Horror"), .init(name: "Action")],
            trackLabel: "Track",
            stopTrackingLabel: "Stop Tracking",
            addToListLabel: "Add To List",
            rateLabel: "Rate",
            onAddToLibrary: {},
            onAddToCustomList: {},
            onRate: {}
        )
        .padding()
        .appPreview()
        .assertSnapshot(testName: "ShowInfoView_SimklNoList")
    }

    func test_ShowInfoView_AlreadyRated() {
        ShowInfoView(
            isFollowed: true,
            canAddToList: true,
            genres: [.init(name: "Sci-Fi"), .init(name: "Horror"), .init(name: "Action")],
            trackLabel: "Track",
            stopTrackingLabel: "Stop Tracking",
            addToListLabel: "Add To List",
            rateLabel: "Rate",
            userRating: 9,
            onAddToLibrary: {},
            onAddToCustomList: {},
            onRate: {}
        )
        .padding()
        .appPreview()
        .assertSnapshot(testName: "ShowInfoView_AlreadyRated")
    }
}
