import SwiftUI

/// Single source of truth for poster/image sizing. Aspect ratios are expressed as width / height
/// (so `height = width / aspect`, matching `PosterItemView`). Size-class-keyed values mirror the
/// Android `Layout` tokens so both platforms resolve the same dimensions at the same width.
public enum ImageDimens {
    public static let posterAspect: CGFloat = 2.0 / 3.0
    public static let backdropAspect: CGFloat = 16.0 / 9.0
    public static let castAspect: CGFloat = 3.0 / 4.0

    public static let gridItemSpacing: CGFloat = 4

    public static func bodyMargin(_ sizeClass: WidthSizeClass) -> CGFloat {
        switch sizeClass {
        case .compact: 16
        case .medium: 24
        case .expanded: 32
        }
    }

    public static func gutter(_ sizeClass: WidthSizeClass) -> CGFloat {
        switch sizeClass {
        case .compact: 8
        case .medium: 12
        case .expanded: 16
        }
    }

    public static func posterColumns(_ sizeClass: WidthSizeClass) -> Int {
        switch sizeClass {
        case .compact: 3
        case .medium: 5
        case .expanded: 7
        }
    }

    /// Flexible `LazyVGrid` columns for a poster grid, count derived from the width size class so the
    /// grid matches the Android `Layout.posterColumns` count at the same width.
    public static func posterGridColumns(_ sizeClass: WidthSizeClass, spacing: CGFloat) -> [GridItem] {
        Array(repeating: GridItem(.flexible(), spacing: spacing), count: posterColumns(sizeClass))
    }

    public static func posterWidth(_ sizeClass: WidthSizeClass) -> CGFloat {
        switch sizeClass {
        case .compact: 112
        case .medium: 140
        case .expanded: 160
        }
    }

    public static func backdropCardWidth(_ sizeClass: WidthSizeClass) -> CGFloat {
        switch sizeClass {
        case .compact: 240
        case .medium: 300
        case .expanded: 360
        }
    }

    public static func castCardWidth(_ sizeClass: WidthSizeClass) -> CGFloat {
        switch sizeClass {
        case .compact: 112
        case .medium: 128
        case .expanded: 144
        }
    }
}
