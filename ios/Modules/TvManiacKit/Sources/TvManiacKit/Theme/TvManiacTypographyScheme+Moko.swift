import i18n
import SwiftUI
import SwiftUIComponents

public extension TvManiacTypographyScheme {
    static let moko = TvManiacTypographyScheme(
        displayLarge: Font(resource: \.work_sans, withSize: 57),
        displayMedium: Font(resource: \.work_sans, withSize: 45),
        displaySmall: Font(resource: \.work_sans, withSize: 36),
        headlineLarge: Font(resource: \.work_sans_bold, withSize: 32),
        headlineMedium: Font(resource: \.work_sans_semibold, withSize: 28),
        headlineSmall: Font(resource: \.work_sans_semibold, withSize: 24),
        titleLarge: Font(resource: \.work_sans_semibold, withSize: 22),
        titleMedium: Font(resource: \.work_sans_semibold, withSize: 16),
        titleSmall: Font(resource: \.work_sans_bold, withSize: 14),
        bodyLarge: Font(resource: \.work_sans, withSize: 16),
        bodyMedium: Font(resource: \.work_sans_medium, withSize: 14),
        bodySmall: Font(resource: \.work_sans, withSize: 12),
        labelLarge: Font(resource: \.work_sans_semibold, withSize: 14),
        labelMedium: Font(resource: \.work_sans_semibold, withSize: 12),
        labelSmall: Font(resource: \.work_sans, withSize: 11)
    )

    static func configureMoko() {
        TvManiacTypographyScheme.shared = .moko
    }
}
