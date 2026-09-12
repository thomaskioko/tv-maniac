import SwiftUI

public extension EnvironmentValues {
    @Entry var liquidGlassEnabled: Bool = false
}

public extension View {
    func liquidGlassEnabled(_ enabled: Bool) -> some View {
        environment(\.liquidGlassEnabled, enabled)
    }

    func liquidGlassVariant(
        @ViewBuilder liquidGlass: @escaping (Self) -> some View,
        @ViewBuilder legacy: @escaping (Self) -> some View
    ) -> some View {
        LiquidGlassVariant(base: self, liquidGlass: liquidGlass, legacy: legacy)
    }
}

private struct LiquidGlassVariant<Base: View, Glass: View, Legacy: View>: View {
    @Environment(\.liquidGlassEnabled) private var liquidGlassEnabled

    let base: Base
    let liquidGlass: (Base) -> Glass
    let legacy: (Base) -> Legacy

    var body: some View {
        if liquidGlassEnabled {
            liquidGlass(base)
        } else {
            legacy(base)
        }
    }
}
