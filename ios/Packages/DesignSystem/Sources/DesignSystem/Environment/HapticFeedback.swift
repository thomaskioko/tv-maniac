import SwiftUI

public extension EnvironmentValues {
    @Entry var hapticFeedbackEnabled: Bool = true
}

public extension View {
    func hapticFeedbackEnabled(_ enabled: Bool) -> some View {
        environment(\.hapticFeedbackEnabled, enabled)
    }
}
