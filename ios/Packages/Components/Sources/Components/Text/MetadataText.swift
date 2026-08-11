import SwiftUI

public func metadataText(_ components: [String], accent: Color) -> Text {
    var joined = AttributedString()
    for (index, component) in components.enumerated() {
        if index > 0 {
            var separator = AttributedString(" · ")
            separator.foregroundColor = accent
            joined.append(separator)
        }
        joined.append(AttributedString(component))
    }
    return Text(joined)
}
