import DesignSystem
import SwiftUI

public extension View {
    /// Sets the same identifier the Android side passes to Compose `Modifier.testTag(...)`.
    func testTag(_ identifier: String) -> some View {
        accessibilityIdentifier(identifier)
    }

    /// No-ops on nil, rather than writing an empty identifier.
    func testTag(_ identifier: String?) -> some View {
        modifier(OptionalTestTag(identifier: identifier))
    }

    /// Also publishes the container as an accessibility element, which SwiftUI skips for plain
    /// layout views. Apply outside every loading, empty and error branch so the tag always renders.
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
