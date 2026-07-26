import DesignSystem
import SwiftUI

public extension View {
    /// Sets an accessibility identifier so the view can be located from XCUITest using
    /// the same string the Android side passes to Compose `Modifier.testTag(...)`.
    /// Pair with constants from the shared `core:test-tags` module, e.g.
    /// `.testTag(DiscoverTestTags.shared.SCREEN_TEST_TAG)`.
    func testTag(_ identifier: String) -> some View {
        accessibilityIdentifier(identifier)
    }

    /// No-ops when `identifier` is nil, so a shared component can stay untagged for the callers
    /// that do not need to reach its items.
    func testTag(_ identifier: String?) -> some View {
        modifier(OptionalTestTag(identifier: identifier))
    }

    /// Tags a screen's outermost container so XCUITest can assert the screen is on
    /// display. Unlike `testTag`, this also publishes the container itself as an
    /// accessibility element, which SwiftUI otherwise skips for plain layout views,
    /// leaving it unreachable through `app.otherElements`. Apply it outside every
    /// loading, empty, and error branch so the tag survives whatever state renders.
    func screenTag(_ identifier: String) -> some View {
        accessibilityElement(children: .contain)
            .accessibilityIdentifier(identifier)
    }
}

private struct OptionalTestTag: ViewModifier {
    let identifier: String?

    func body(content: Content) -> some View {
        if let identifier {
            content.accessibilityIdentifier(identifier)
        } else {
            content
        }
    }
}
