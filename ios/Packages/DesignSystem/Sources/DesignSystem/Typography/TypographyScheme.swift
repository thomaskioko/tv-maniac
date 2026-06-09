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
                WorkSansFontRegistrar.registerIfNeeded()
                _shared = .workSans
                return _shared!
            #else
                fatalError("TvManiacTypographyScheme.configure() must be called before accessing typography")
            #endif
        }
        set {
            _shared = newValue
        }
    }

    public static func configure() {
        WorkSansFontRegistrar.registerIfNeeded()
        shared = .workSans
    }

    static let preview = TvManiacTypographyScheme(
        displayLarge: style(size: 57, weight: .medium, tracking: -0.25, lineHeight: 64),
        displayMedium: style(size: 45, weight: .medium, tracking: 0, lineHeight: 52),
        displaySmall: style(size: 36, weight: .medium, tracking: 0, lineHeight: 44),
        headlineLarge: style(size: 32, weight: .bold, tracking: 0, lineHeight: 40),
        headlineLargeEmphasized: style(size: 32, weight: .heavy, tracking: 0, lineHeight: 40),
        headlineMedium: style(size: 28, weight: .bold, tracking: 0, lineHeight: 36),
        headlineSmall: style(size: 24, weight: .bold, tracking: 0, lineHeight: 32),
        titleLarge: style(size: 22, weight: .bold, tracking: 0, lineHeight: 28),
        titleLargeEmphasized: style(size: 22, weight: .heavy, tracking: 0, lineHeight: 28),
        titleMedium: style(size: 16, weight: .bold, tracking: 0.15, lineHeight: 24),
        titleSmall: style(size: 14, weight: .heavy, tracking: 0.1, lineHeight: 20),
        bodyLarge: style(size: 16, weight: .medium, tracking: 0.15, lineHeight: 24),
        bodyLargeEmphasized: style(size: 16, weight: .heavy, tracking: 0.15, lineHeight: 24),
        bodyMedium: style(size: 14, weight: .semibold, tracking: 0.25, lineHeight: 20),
        bodySmall: style(size: 12, weight: .medium, tracking: 0.4, lineHeight: 16),
        labelLarge: style(size: 14, weight: .bold, tracking: 0.1, lineHeight: 20),
        labelMedium: style(size: 12, weight: .bold, tracking: 0.5, lineHeight: 16),
        labelSmall: style(size: 11, weight: .medium, tracking: 0.5, lineHeight: 16)
    )

    static let workSans = TvManiacTypographyScheme(
        displayLarge: workSansStyle(.medium, size: 57, relativeTo: .largeTitle, tracking: -0.25, lineHeight: 64),
        displayMedium: workSansStyle(.medium, size: 45, relativeTo: .largeTitle, tracking: 0, lineHeight: 52),
        displaySmall: workSansStyle(.medium, size: 36, relativeTo: .largeTitle, tracking: 0, lineHeight: 44),
        headlineLarge: workSansStyle(.bold, size: 32, relativeTo: .title, tracking: 0, lineHeight: 40),
        headlineLargeEmphasized: workSansStyle(.extrabold, size: 32, relativeTo: .title, tracking: 0, lineHeight: 40),
        headlineMedium: workSansStyle(.bold, size: 28, relativeTo: .title, tracking: 0, lineHeight: 36),
        headlineSmall: workSansStyle(.bold, size: 24, relativeTo: .title2, tracking: 0, lineHeight: 32),
        titleLarge: workSansStyle(.bold, size: 22, relativeTo: .title2, tracking: 0, lineHeight: 28),
        titleLargeEmphasized: workSansStyle(.extrabold, size: 22, relativeTo: .title2, tracking: 0, lineHeight: 28),
        titleMedium: workSansStyle(.bold, size: 16, relativeTo: .callout, tracking: 0.15, lineHeight: 24),
        titleSmall: workSansStyle(.extrabold, size: 14, relativeTo: .subheadline, tracking: 0.1, lineHeight: 20),
        bodyLarge: workSansStyle(.medium, size: 16, relativeTo: .body, tracking: 0.15, lineHeight: 24),
        bodyLargeEmphasized: workSansStyle(.extrabold, size: 16, relativeTo: .body, tracking: 0.15, lineHeight: 24),
        bodyMedium: workSansStyle(.semibold, size: 14, relativeTo: .subheadline, tracking: 0.25, lineHeight: 20),
        bodySmall: workSansStyle(.medium, size: 12, relativeTo: .caption, tracking: 0.4, lineHeight: 16),
        labelLarge: workSansStyle(.bold, size: 14, relativeTo: .footnote, tracking: 0.1, lineHeight: 20),
        labelMedium: workSansStyle(.bold, size: 12, relativeTo: .caption, tracking: 0.5, lineHeight: 16),
        labelSmall: workSansStyle(.medium, size: 11, relativeTo: .caption2, tracking: 0.5, lineHeight: 16)
    )

    public let displayLarge: TvManiacTextStyle
    public let displayMedium: TvManiacTextStyle
    public let displaySmall: TvManiacTextStyle
    public let headlineLarge: TvManiacTextStyle
    public let headlineLargeEmphasized: TvManiacTextStyle
    public let headlineMedium: TvManiacTextStyle
    public let headlineSmall: TvManiacTextStyle
    public let titleLarge: TvManiacTextStyle
    public let titleLargeEmphasized: TvManiacTextStyle
    public let titleMedium: TvManiacTextStyle
    public let titleSmall: TvManiacTextStyle
    public let bodyLarge: TvManiacTextStyle
    public let bodyLargeEmphasized: TvManiacTextStyle
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
        headlineLargeEmphasized: TvManiacTextStyle,
        headlineMedium: TvManiacTextStyle,
        headlineSmall: TvManiacTextStyle,
        titleLarge: TvManiacTextStyle,
        titleLargeEmphasized: TvManiacTextStyle,
        titleMedium: TvManiacTextStyle,
        titleSmall: TvManiacTextStyle,
        bodyLarge: TvManiacTextStyle,
        bodyLargeEmphasized: TvManiacTextStyle,
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
        self.headlineLargeEmphasized = headlineLargeEmphasized
        self.headlineMedium = headlineMedium
        self.headlineSmall = headlineSmall
        self.titleLarge = titleLarge
        self.titleLargeEmphasized = titleLargeEmphasized
        self.titleMedium = titleMedium
        self.titleSmall = titleSmall
        self.bodyLarge = bodyLarge
        self.bodyLargeEmphasized = bodyLargeEmphasized
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

    private static func workSansStyle(
        _ weight: WorkSans,
        size: CGFloat,
        relativeTo textStyle: Font.TextStyle,
        tracking: CGFloat,
        lineHeight: CGFloat
    ) -> TvManiacTextStyle {
        TvManiacTextStyle(
            font: .workSans(weight, size: size, relativeTo: textStyle),
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
            WorkSansFontRegistrar.registerIfNeeded()
            _shared = .workSans
        }
    }
#endif
