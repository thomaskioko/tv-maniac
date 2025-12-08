import SnapshotTestingLib
import SwiftUI
import SwiftUIComponents
import XCTest

class ProviderItemViewTest: SnapshotTestCase {
    func test_ProviderItemView() {
        ProviderItemView(
            logoUrl: ""
        )
        .padding()
        .themedPreview()
        .assertSnapshot(testName: "ProviderItemView")
    }
}
