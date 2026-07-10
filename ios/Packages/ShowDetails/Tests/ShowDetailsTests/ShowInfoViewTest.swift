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
            isInList: false,
            genres: [.init(name: "Sci-Fi"), .init(name: "Horror"), .init(name: "Action")],
            trackLabel: "Track",
            stopTrackingLabel: "Stop Tracking",
            listActionLabel: "Add To List",
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
            isInList: false,
            genres: [.init(name: "Drama"), .init(name: "Fantasy"), .init(name: "Adventure")],
            trackLabel: "Track",
            stopTrackingLabel: "Stop Tracking",
            listActionLabel: "Add To List",
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
            isInList: false,
            genres: [.init(name: "Sci-Fi"), .init(name: "Horror"), .init(name: "Action")],
            trackLabel: "Track",
            stopTrackingLabel: "Stop Tracking",
            listActionLabel: "Add To List",
            rateLabel: "Rate",
            onAddToLibrary: {},
            onAddToCustomList: {},
            onRate: {}
        )
        .padding()
        .appPreview()
        .assertSnapshot(testName: "ShowInfoView_SimklNoList")
    }

    func test_ShowInfoView_InList() {
        ShowInfoView(
            isFollowed: true,
            canAddToList: true,
            isInList: true,
            genres: [.init(name: "Sci-Fi"), .init(name: "Horror"), .init(name: "Action")],
            trackLabel: "Track",
            stopTrackingLabel: "Stop Tracking",
            listActionLabel: "In List",
            rateLabel: "Rate",
            onAddToLibrary: {},
            onAddToCustomList: {},
            onRate: {}
        )
        .padding()
        .appPreview()
        .assertSnapshot(testName: "ShowInfoView_InList")
    }

    func test_ShowInfoView_AlreadyRated() {
        ShowInfoView(
            isFollowed: true,
            canAddToList: true,
            isInList: false,
            genres: [.init(name: "Sci-Fi"), .init(name: "Horror"), .init(name: "Action")],
            trackLabel: "Track",
            stopTrackingLabel: "Stop Tracking",
            listActionLabel: "Add To List",
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
