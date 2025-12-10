import SwiftUI

public protocol TvManiacTheme {
    var colors: TvManiacColorScheme { get }
    var typography: TvManiacTypographyScheme { get }
    var spacing: TvManiacSpacingScheme { get }
    var shapes: TvManiacShapeScheme { get }
    var scanlineConfig: ScanlineConfiguration { get }
}

public extension TvManiacTheme {
    var scanlineConfig: ScanlineConfiguration { .disabled }
}

public struct LightTheme: TvManiacTheme {
    public let colors = TvManiacColorScheme.light
    public var typography: TvManiacTypographyScheme { .shared }
    public let spacing = TvManiacSpacingScheme.default
    public let shapes = TvManiacShapeScheme.default

    public init() {}
}

public struct DarkTheme: TvManiacTheme {
    public let colors = TvManiacColorScheme.dark
    public var typography: TvManiacTypographyScheme { .shared }
    public let spacing = TvManiacSpacingScheme.default
    public let shapes = TvManiacShapeScheme.default

    public init() {}
}

public struct TerminalTheme: TvManiacTheme {
    public let colors = TvManiacColorScheme.terminal
    public var typography: TvManiacTypographyScheme { .shared }
    public let spacing = TvManiacSpacingScheme.default
    public let shapes = TvManiacShapeScheme.default
    public let scanlineConfig = ScanlineConfiguration.terminal()

    public init() {}
}

public struct AutumnTheme: TvManiacTheme {
    public let colors = TvManiacColorScheme.autumn
    public var typography: TvManiacTypographyScheme { .shared }
    public let spacing = TvManiacSpacingScheme.default
    public let shapes = TvManiacShapeScheme.default

    public init() {}
}

public struct AquaTheme: TvManiacTheme {
    public let colors = TvManiacColorScheme.aqua
    public var typography: TvManiacTypographyScheme { .shared }
    public let spacing = TvManiacSpacingScheme.default
    public let shapes = TvManiacShapeScheme.default

    public init() {}
}

public struct AmberTheme: TvManiacTheme {
    public let colors = TvManiacColorScheme.amber
    public var typography: TvManiacTypographyScheme { .shared }
    public let spacing = TvManiacSpacingScheme.default
    public let shapes = TvManiacShapeScheme.default
    public let scanlineConfig = ScanlineConfiguration.amber()

    public init() {}
}

public struct SnowTheme: TvManiacTheme {
    public let colors = TvManiacColorScheme.snow
    public var typography: TvManiacTypographyScheme { .shared }
    public let spacing = TvManiacSpacingScheme.default
    public let shapes = TvManiacShapeScheme.default
    public let scanlineConfig = ScanlineConfiguration.snow()

    public init() {}
}

public struct CrimsonTheme: TvManiacTheme {
    public let colors = TvManiacColorScheme.crimson
    public var typography: TvManiacTypographyScheme { .shared }
    public let spacing = TvManiacSpacingScheme.default
    public let shapes = TvManiacShapeScheme.default
    public let scanlineConfig = ScanlineConfiguration.crimson()

    public init() {}
}
