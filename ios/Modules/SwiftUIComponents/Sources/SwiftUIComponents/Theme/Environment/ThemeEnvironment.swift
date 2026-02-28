import SwiftUI

public struct TvManiacThemeKey: EnvironmentKey {
    public static let defaultValue: TvManiacTheme = LightTheme()
}

public extension EnvironmentValues {
    var tvManiacTheme: TvManiacTheme {
        get { self[TvManiacThemeKey.self] }
        set { self[TvManiacThemeKey.self] = newValue }
    }
}

public extension View {
    func tvManiacTheme(_ theme: TvManiacTheme) -> some View {
        environment(\.tvManiacTheme, theme)
    }
}

@propertyWrapper
public struct Theme: DynamicProperty {
    @Environment(\.tvManiacTheme) private var theme: TvManiacTheme

    public var wrappedValue: TvManiacTheme {
        theme
    }

    public init() {}
}
