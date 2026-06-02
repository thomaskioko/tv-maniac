import Components
import DesignSystem
import Models
import ShowDetails
import SnapshotTestingLib
import SwiftUI
import XCTest

class ProviderListViewTest: SnapshotTestCase {
    func test_ProviderListView() {
        ProviderListView(
            items: [
                .init(
                    providerId: 123,
                    logoUrl: ""
                ),
                .init(
                    providerId: 1233,
                    logoUrl: ""
                ),
                .init(
                    providerId: 23,
                    logoUrl: ""
                ),
            ]
        )
        .appPreview()
        .assertSnapshot(testName: "ProviderList")
    }
}
