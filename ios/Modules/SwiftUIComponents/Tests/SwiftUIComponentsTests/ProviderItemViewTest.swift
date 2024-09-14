import SnapshotTestingLib
import SwiftUI
import SwiftUIComponents
import XCTest

class ProviderItemViewTest: XCTestCase {
    func test_ProviderItemView() {
        ProviderItemView(
            logoUrl: "https://image.tmdb.org/t/p/w780/https://image.tmdb.org/t/p/w780/aYkLXz4dxHgOrFNH7Jv7Cpy56Ms.png"
        )
        .padding()
        .background(Color.background)
        .assertSnapshot(testName: "ProviderItemView")
    }
}
