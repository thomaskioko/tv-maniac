import SwiftUI

public extension View {
    /// Sets an accessibility identifier so the view can be located from XCUITest using
    /// the same string the Android side passes to Compose `Modifier.testTag(...)`.
    /// Pair with constants from the shared `core:test-tags` module, e.g.
    /// `.testTag(DiscoverTestTags.shared.SCREEN_TEST_TAG)`.
    func testTag(_ identifier: String) -> some View {
        accessibilityIdentifier(identifier)
    }
}
