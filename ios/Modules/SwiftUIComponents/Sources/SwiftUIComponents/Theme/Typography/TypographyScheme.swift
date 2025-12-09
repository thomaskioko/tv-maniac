import SwiftUI

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
        displayLarge: .system(size: 57),
        displayMedium: .system(size: 45),
        displaySmall: .system(size: 36),
        headlineLarge: .system(size: 32, weight: .bold),
        headlineMedium: .system(size: 28, weight: .semibold),
        headlineSmall: .system(size: 24, weight: .semibold),
        titleLarge: .system(size: 22, weight: .semibold),
        titleMedium: .system(size: 16, weight: .semibold),
        titleSmall: .system(size: 14, weight: .bold),
        bodyLarge: .system(size: 16),
        bodyMedium: .system(size: 14, weight: .medium),
        bodySmall: .system(size: 12),
        labelLarge: .system(size: 14, weight: .semibold),
        labelMedium: .system(size: 12, weight: .semibold),
        labelSmall: .system(size: 11)
    )

    public let displayLarge: Font
    public let displayMedium: Font
    public let displaySmall: Font
    public let headlineLarge: Font
    public let headlineMedium: Font
    public let headlineSmall: Font
    public let titleLarge: Font
    public let titleMedium: Font
    public let titleSmall: Font
    public let bodyLarge: Font
    public let bodyMedium: Font
    public let bodySmall: Font
    public let labelLarge: Font
    public let labelMedium: Font
    public let labelSmall: Font

    public init(
        displayLarge: Font,
        displayMedium: Font,
        displaySmall: Font,
        headlineLarge: Font,
        headlineMedium: Font,
        headlineSmall: Font,
        titleLarge: Font,
        titleMedium: Font,
        titleSmall: Font,
        bodyLarge: Font,
        bodyMedium: Font,
        bodySmall: Font,
        labelLarge: Font,
        labelMedium: Font,
        labelSmall: Font
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
}

public extension View {
    func textStyle(_ font: Font) -> some View {
        self.font(font)
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
