import DesignSystem
import i18n
import SwiftUI
import TvManiac
import UIKit

public extension TvManiacTypographyScheme {
    static let moko = TvManiacTypographyScheme(
        displayLarge: style(\.work_sans_medium, size: 57, tracking: -0.25, lineHeight: 64),
        displayMedium: style(\.work_sans_medium, size: 45, tracking: 0, lineHeight: 52),
        displaySmall: style(\.work_sans_medium, size: 36, tracking: 0, lineHeight: 44),
        headlineLarge: style(\.work_sans_bold, size: 32, tracking: 0, lineHeight: 40),
        headlineMedium: style(\.work_sans_bold, size: 28, tracking: 0, lineHeight: 36),
        headlineSmall: style(\.work_sans_bold, size: 24, tracking: 0, lineHeight: 32),
        titleLarge: style(\.work_sans_bold, size: 22, tracking: 0, lineHeight: 28),
        titleMedium: style(\.work_sans_bold, size: 16, tracking: 0.15, lineHeight: 24),
        titleSmall: style(\.work_sans_extrabold, size: 14, tracking: 0.1, lineHeight: 20),
        bodyLarge: style(\.work_sans_medium, size: 16, tracking: 0.15, lineHeight: 24),
        bodyMedium: style(\.work_sans_semibold, size: 14, tracking: 0.25, lineHeight: 20),
        bodySmall: style(\.work_sans_medium, size: 12, tracking: 0.4, lineHeight: 16),
        labelLarge: style(\.work_sans_bold, size: 14, tracking: 0.1, lineHeight: 20),
        labelMedium: style(\.work_sans_bold, size: 12, tracking: 0.5, lineHeight: 16),
        labelSmall: style(\.work_sans_medium, size: 11, tracking: 0.5, lineHeight: 16)
    )

    static func configureMoko() {
        TvManiacTypographyScheme.shared = .moko
    }

    private static func style(
        _ resource: KeyPath<MR.fonts, FontResource>,
        size: Double,
        tracking: CGFloat,
        lineHeight: CGFloat
    ) -> TvManiacTextStyle {
        let uiFont = MR.fonts()[keyPath: resource].uiFont(withSize: size)
        return TvManiacTextStyle(
            font: Font(uiFont),
            tracking: tracking,
            lineSpacing: max(0, lineHeight - uiFont.lineHeight)
        )
    }
}
