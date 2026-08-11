import DesignSystem
import SwiftUI

public extension View {
    func testTag(_ identifier: String) -> some View {
        accessibilityIdentifier(identifier)
    }

    func testTag(_ identifier: String?) -> some View {
        modifier(OptionalTestTag(identifier: identifier))
    }

    func screenTag(_ identifier: String) -> some View {
        accessibilityElement(children: .contain)
            .accessibilityIdentifier(identifier)
    }

    func screenTag(_ identifier: String?) -> some View {
        modifier(OptionalScreenTag(identifier: identifier))
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

private struct OptionalScreenTag: ViewModifier {
    let identifier: String?

    func body(content: Content) -> some View {
        if let identifier {
            content
                .accessibilityElement(children: .contain)
                .accessibilityIdentifier(identifier)
        } else {
            content
        }
    }
}
