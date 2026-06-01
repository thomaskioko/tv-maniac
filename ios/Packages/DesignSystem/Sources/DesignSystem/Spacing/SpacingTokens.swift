import SwiftUI

public struct TvManiacSpacingScheme {
    public let none: CGFloat
    public let xxxSmall: CGFloat
    public let xxSmall: CGFloat
    public let xSmall: CGFloat
    public let small: CGFloat
    public let medium: CGFloat
    public let large: CGFloat
    public let xLarge: CGFloat
    public let xxLarge: CGFloat
    public let xxxLarge: CGFloat

    public init(
        none: CGFloat = 0,
        xxxSmall: CGFloat = 2,
        xxSmall: CGFloat = 4,
        xSmall: CGFloat = 8,
        small: CGFloat = 12,
        medium: CGFloat = 16,
        large: CGFloat = 24,
        xLarge: CGFloat = 32,
        xxLarge: CGFloat = 48,
        xxxLarge: CGFloat = 64
    ) {
        self.none = none
        self.xxxSmall = xxxSmall
        self.xxSmall = xxSmall
        self.xSmall = xSmall
        self.small = small
        self.medium = medium
        self.large = large
        self.xLarge = xLarge
        self.xxLarge = xxLarge
        self.xxxLarge = xxxLarge
    }
}

public extension TvManiacSpacingScheme {
    static let `default` = TvManiacSpacingScheme()
}
