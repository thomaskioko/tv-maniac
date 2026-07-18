import SwiftUI

public struct TvManiacColorScheme {
    public let primary: Color
    public let primaryContainer: Color
    public let onPrimary: Color
    public let secondary: Color
    public let onSecondary: Color
    public let error: Color
    public let onError: Color
    public let background: Color
    public let onBackground: Color
    public let surface: Color
    public let onSurface: Color
    public let surfaceVariant: Color
    public let onSurfaceVariant: Color
    public let outline: Color
    public let accent: Color
    public let onAccent: Color
    public let buttonBackground: Color
    public let onButtonBackground: Color
    public let success: Color
    public let onSuccess: Color
    public let syncing: Color
    public let grey: Color
    public let scrim: Color
    public let onScrim: Color

    public init(
        primary: Color,
        primaryContainer: Color,
        onPrimary: Color,
        secondary: Color,
        onSecondary: Color,
        error: Color,
        onError: Color,
        background: Color,
        onBackground: Color,
        surface: Color,
        onSurface: Color,
        surfaceVariant: Color,
        onSurfaceVariant: Color,
        outline: Color,
        accent: Color,
        onAccent: Color,
        buttonBackground: Color,
        onButtonBackground: Color,
        success: Color = Color(hex: "00B300"),
        onSuccess: Color = .white,
        syncing: Color = Color(hex: "CC5500"),
        grey: Color = Color(hex: "808080"),
        scrim: Color = .black,
        onScrim: Color = .white
    ) {
        self.primary = primary
        self.primaryContainer = primaryContainer
        self.onPrimary = onPrimary
        self.secondary = secondary
        self.onSecondary = onSecondary
        self.error = error
        self.onError = onError
        self.background = background
        self.onBackground = onBackground
        self.surface = surface
        self.onSurface = onSurface
        self.surfaceVariant = surfaceVariant
        self.onSurfaceVariant = onSurfaceVariant
        self.outline = outline
        self.accent = accent
        self.onAccent = onAccent
        self.buttonBackground = buttonBackground
        self.onButtonBackground = onButtonBackground
        self.success = success
        self.onSuccess = onSuccess
        self.syncing = syncing
        self.grey = grey
        self.scrim = scrim
        self.onScrim = onScrim
    }
}

public extension TvManiacColorScheme {
    func backgroundGradient() -> [Color] {
        [
            background,
            background.opacity(0.9),
            background.opacity(0.8),
            background.opacity(0.7),
            Color.clear,
        ]
    }

    func imageGradient() -> LinearGradient {
        LinearGradient(
            colors: [
                scrim.opacity(0),
                scrim.opacity(0.383),
                scrim.opacity(0.707),
                scrim.opacity(0.924),
                scrim,
            ],
            startPoint: .top,
            endPoint: .bottom
        )
    }
}
