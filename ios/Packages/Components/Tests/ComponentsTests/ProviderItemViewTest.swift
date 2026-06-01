import Components
import DesignSystem
import Models
import SnapshotTestingLib
import SwiftUI
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
