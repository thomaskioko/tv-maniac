import SnapshotTestingLib
import SwiftUI
import SwiftUIComponents
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
        .themedPreview()
        .assertSnapshot(testName: "ProviderList")
    }
}
