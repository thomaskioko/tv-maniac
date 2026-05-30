import SwiftUI

public struct TvManiacTextStyle {
    public let font: Font
    public let tracking: CGFloat
    public let lineSpacing: CGFloat

    public init(font: Font, tracking: CGFloat, lineSpacing: CGFloat) {
        self.font = font
        self.tracking = tracking
        self.lineSpacing = lineSpacing
    }
}

public struct TvManiacTypographyScheme {
    private static var _shared: TvManiacTypographyScheme?

    public static var shared: TvManiacTypographyScheme {
        get {
            if let shared = _shared {
                return shared
            }
            #if DEBUG
                _shared = .preview
                return _shared!
            #else
                fatalError("TvManiacTypographyScheme.configure() must be called before accessing typography")
            #endif
        }
        set {
            _shared = newValue
        }
    }

    static let preview = TvManiacTypographyScheme(
        displayLarge: style(size: 57, weight: .medium, tracking: -0.25, lineHeight: 64),
        displayMedium: style(size: 45, weight: .medium, tracking: 0, lineHeight: 52),
        displaySmall: style(size: 36, weight: .medium, tracking: 0, lineHeight: 44),
        headlineLarge: style(size: 32, weight: .bold, tracking: 0, lineHeight: 40),
        headlineMedium: style(size: 28, weight: .bold, tracking: 0, lineHeight: 36),
        headlineSmall: style(size: 24, weight: .bold, tracking: 0, lineHeight: 32),
        titleLarge: style(size: 22, weight: .bold, tracking: 0, lineHeight: 28),
        titleMedium: style(size: 16, weight: .bold, tracking: 0.15, lineHeight: 24),
        titleSmall: style(size: 14, weight: .heavy, tracking: 0.1, lineHeight: 20),
        bodyLarge: style(size: 16, weight: .medium, tracking: 0.15, lineHeight: 24),
        bodyMedium: style(size: 14, weight: .semibold, tracking: 0.25, lineHeight: 20),
        bodySmall: style(size: 12, weight: .medium, tracking: 0.4, lineHeight: 16),
        labelLarge: style(size: 14, weight: .bold, tracking: 0.1, lineHeight: 20),
        labelMedium: style(size: 12, weight: .bold, tracking: 0.5, lineHeight: 16),
        labelSmall: style(size: 11, weight: .medium, tracking: 0.5, lineHeight: 16)
    )

    public let displayLarge: TvManiacTextStyle
    public let displayMedium: TvManiacTextStyle
    public let displaySmall: TvManiacTextStyle
    public let headlineLarge: TvManiacTextStyle
    public let headlineMedium: TvManiacTextStyle
    public let headlineSmall: TvManiacTextStyle
    public let titleLarge: TvManiacTextStyle
    public let titleMedium: TvManiacTextStyle
    public let titleSmall: TvManiacTextStyle
    public let bodyLarge: TvManiacTextStyle
    public let bodyMedium: TvManiacTextStyle
    public let bodySmall: TvManiacTextStyle
    public let labelLarge: TvManiacTextStyle
    public let labelMedium: TvManiacTextStyle
    public let labelSmall: TvManiacTextStyle

    public init(
        displayLarge: TvManiacTextStyle,
        displayMedium: TvManiacTextStyle,
        displaySmall: TvManiacTextStyle,
        headlineLarge: TvManiacTextStyle,
        headlineMedium: TvManiacTextStyle,
        headlineSmall: TvManiacTextStyle,
        titleLarge: TvManiacTextStyle,
        titleMedium: TvManiacTextStyle,
        titleSmall: TvManiacTextStyle,
        bodyLarge: TvManiacTextStyle,
        bodyMedium: TvManiacTextStyle,
        bodySmall: TvManiacTextStyle,
        labelLarge: TvManiacTextStyle,
        labelMedium: TvManiacTextStyle,
        labelSmall: TvManiacTextStyle
    ) {
        self.displayLarge = displayLarge
        self.displayMedium = displayMedium
        self.displaySmall = displaySmall
        self.headlineLarge = headlineLarge
        self.headlineMedium = headlineMedium
        self.headlineSmall = headlineSmall
        self.titleLarge = titleLarge
        self.titleMedium = titleMedium
        self.titleSmall = titleSmall
        self.bodyLarge = bodyLarge
        self.bodyMedium = bodyMedium
        self.bodySmall = bodySmall
        self.labelLarge = labelLarge
        self.labelMedium = labelMedium
        self.labelSmall = labelSmall
    }

    private static func style(
        size: CGFloat,
        weight: Font.Weight,
        tracking: CGFloat,
        lineHeight: CGFloat
    ) -> TvManiacTextStyle {
        TvManiacTextStyle(
            font: .system(size: size, weight: weight),
            tracking: tracking,
            lineSpacing: max(0, lineHeight - size * 1.2)
        )
    }
}

public extension View {
    func textStyle(_ font: Font) -> some View {
        self.font(font)
    }

    func textStyle(_ style: TvManiacTextStyle) -> some View {
        font(style.font)
            .tracking(style.tracking)
            .lineSpacing(style.lineSpacing)
    }
}

#if DEBUG
    public extension TvManiacTypographyScheme {
        static func configureForTesting() {
            guard _shared == nil else { return }
            _shared = .preview
        }
    }
#endif
