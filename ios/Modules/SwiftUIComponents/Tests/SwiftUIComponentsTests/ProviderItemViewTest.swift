import SnapshotTestingLib
import SwiftUI
import DesignSystem
import SwiftUIComponents
import XCTest

class ProviderItemViewTest: SnapshotTestCase {
    func test_ProviderItemView() {
        ProviderItemView(
            logoUrl: ""
        )
        .padding()
        .appPreview()
        .assertSnapshot(testName: "ProviderItemView")
    }
}
