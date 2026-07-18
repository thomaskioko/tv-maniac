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

    private static var scalePercent = 100

    public static func updateFontScale(percent: Int) {
        guard percent != scalePercent else { return }
        scalePercent = percent
        shared = percent == 100 ? .workSans : scaledWorkSans(scale: CGFloat(percent) / 100)
    }

    static let workSans = scaledWorkSans(scale: 1)

    private static func scaledWorkSans(scale: CGFloat) -> TvManiacTypographyScheme {
        TvManiacTypographyScheme(
            displayLarge: workSansStyle(.medium, size: 57 * scale, relativeTo: .largeTitle, tracking: -0.25, lineHeight: 64 * scale),
            displayMedium: workSansStyle(.medium, size: 45 * scale, relativeTo: .largeTitle, tracking: 0, lineHeight: 52 * scale),
            displaySmall: workSansStyle(.extrabold, size: 36 * scale, relativeTo: .largeTitle, tracking: 0, lineHeight: 44 * scale),
            headlineLarge: workSansStyle(.bold, size: 32 * scale, relativeTo: .title, tracking: 0, lineHeight: 40 * scale),
            headlineMedium: workSansStyle(.bold, size: 28 * scale, relativeTo: .title, tracking: 0, lineHeight: 36 * scale),
            headlineSmall: workSansStyle(.bold, size: 24 * scale, relativeTo: .title2, tracking: 0, lineHeight: 32 * scale),
            titleLarge: workSansStyle(.bold, size: 22 * scale, relativeTo: .title2, tracking: 0, lineHeight: 28 * scale),
            titleMedium: workSansStyle(.bold, size: 16 * scale, relativeTo: .subheadline, tracking: 0.15, lineHeight: 24 * scale),
            titleSmall: workSansStyle(.extrabold, size: 14 * scale, relativeTo: .subheadline, tracking: 0.1, lineHeight: 20 * scale),
            bodyLarge: workSansStyle(.medium, size: 16 * scale, relativeTo: .body, tracking: 0.15, lineHeight: 24 * scale),
            bodyMedium: workSansStyle(.medium, size: 14 * scale, relativeTo: .subheadline, tracking: 0.25, lineHeight: 20 * scale),
            bodySmall: workSansStyle(.medium, size: 12 * scale, relativeTo: .caption, tracking: 0.4, lineHeight: 16 * scale),
            labelLarge: workSansStyle(.semibold, size: 14 * scale, relativeTo: .footnote, tracking: 0.1, lineHeight: 20 * scale),
            labelMedium: workSansStyle(.semibold, size: 12 * scale, relativeTo: .caption, tracking: 0.5, lineHeight: 16 * scale),
            labelSmall: workSansStyle(.semibold, size: 11 * scale, relativeTo: .caption2, tracking: 0.5, lineHeight: 16 * scale)
        )
    }

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
