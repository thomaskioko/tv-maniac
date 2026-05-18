import SwiftUI

public struct TvManiacShadowToken {
    public let color: Color
    public let radius: CGFloat
    public let x: CGFloat
    public let y: CGFloat

    public init(
        color: Color,
        radius: CGFloat,
        x: CGFloat = 0,
        y: CGFloat = 0
    ) {
        self.color = color
        self.radius = radius
        self.x = x
        self.y = y
    }

    public static let none = TvManiacShadowToken(color: .clear, radius: 0)
}

public struct TvManiacShadowScheme {
    public let small: TvManiacShadowToken
    public let medium: TvManiacShadowToken
    public let large: TvManiacShadowToken

    public init(
        small: TvManiacShadowToken = TvManiacShadowToken(color: .black.opacity(0.1), radius: 2, y: 1),
        medium: TvManiacShadowToken = TvManiacShadowToken(color: .black.opacity(0.15), radius: 4, y: 2),
        large: TvManiacShadowToken = TvManiacShadowToken(color: .black.opacity(0.2), radius: 8, y: 4)
    ) {
        self.small = small
        self.medium = medium
        self.large = large
    }
}

public extension TvManiacShadowScheme {
    static let `default` = TvManiacShadowScheme()
}

public extension View {
    func appShadow(_ token: TvManiacShadowToken) -> some View {
        shadow(color: token.color, radius: token.radius, x: token.x, y: token.y)
    }

    func appShadow(_ token: TvManiacShadowToken, color: Color) -> some View {
        shadow(color: color, radius: token.radius, x: token.x, y: token.y)
    }
}
