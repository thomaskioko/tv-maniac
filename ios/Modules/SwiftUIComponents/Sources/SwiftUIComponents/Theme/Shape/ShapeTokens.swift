import SwiftUI

public struct TvManiacShapeScheme {
    public let small: CGFloat
    public let medium: CGFloat
    public let large: CGFloat
    public let extraLarge: CGFloat

    public init(
        small: CGFloat = 4,
        medium: CGFloat = 8,
        large: CGFloat = 16,
        extraLarge: CGFloat = 24
    ) {
        self.small = small
        self.medium = medium
        self.large = large
        self.extraLarge = extraLarge
    }
}

public extension TvManiacShapeScheme {
    static let `default` = TvManiacShapeScheme()
}
